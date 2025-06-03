package Controller;

import Boundary.GameView;
import Entity.*;
import java.awt.Point;

public class GameController {
    private GameState gameState;
    private GameView gameView;

    private Piece selectedPiece = null;
    private MoveCard selectedCard = null;

    public GameController() {
        this.gameState = new GameState();
    }

    public void setView(GameView gameView) {
        this.gameView = gameView;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setSelectedPiece(Piece selectedPiece) {
        this.selectedPiece = selectedPiece;
        if (gameView != null) {
            gameView.highlightSelectedPiece(this.selectedPiece);
        }
    }

    public void setSelectedCard(MoveCard selectedCard) {
        this.selectedCard = selectedCard;
        if (gameView != null) {
            gameView.highlightSelectedCard(this.selectedCard);
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public MoveCard getSelectedCard() {
        return selectedCard;
    }

    public void startGame() {
        gameState.initializeGameSetup(); 
        if (gameView != null) {
            gameView.updateView(); 
            gameView.showMessage("Game started. Player " + gameState.getCurrentPlayerId() + "'s turn.");
        }
    }

    public void resetGame() {
        gameState.resetGame(); 
        selectedPiece = null;
        selectedCard = null;
        if (gameView != null) {
            startGame(); 
        }
    }

    public void handleCellClick(int row, int col) {
        if (gameState.isGameOver()) return;
        if (gameView == null) return; 

        Player currentPlayer = gameState.getCurrentPlayer();
        Point clickedPoint = new Point(row, col);

        Piece pieceAtClick = gameState.getBoard().getPieceAt(row, col);

        if (selectedPiece == null) {
            if (pieceAtClick != null && pieceAtClick.isBelongTo(currentPlayer)) {
                setSelectedPiece(pieceAtClick); 
                if (selectedCard != null) {
                    gameView.showPossibleMoves(
                            gameState.getPossibleMoves(currentPlayer, selectedPiece, selectedCard));
                }
            } else {
                gameView.showMessage("Invalid piece selection. Select your piece.");
            }
        } else { 
            if (pieceAtClick != null && pieceAtClick.isBelongTo(currentPlayer)) {
                setSelectedPiece(pieceAtClick); 
                if (selectedCard != null) {
                    gameView.showPossibleMoves(
                            gameState.getPossibleMoves(currentPlayer, selectedPiece, selectedCard));
                } else {
                    gameView.clearPossibleMoves(); 
                }
                return; 
            }

            if (selectedCard == null) {
                gameView.showMessage("Please select a move card first.");
                return;
            }

            final Piece pieceToMove = this.selectedPiece; 
            final MoveCard cardToPlay = this.selectedCard; 
            final Point startPosition = new Point(pieceToMove.getPosX(), pieceToMove.getPosY()); 
            final Point targetPosition = clickedPoint; 

            Piece victimPiece = gameState.getBoard().getPieceAt(targetPosition.x, targetPosition.y);
            final boolean isCapture = (victimPiece != null && victimPiece.getPlayerId() != currentPlayer.getId());
            final Piece capturedPieceForAnimation = isCapture ? victimPiece : null; 
            final Point victimOriginalPos = isCapture && victimPiece != null ? new Point(victimPiece.getPosX(), victimPiece.getPosY()) : null;


            boolean moveSuccessful = gameState.playTurn(pieceToMove, cardToPlay, targetPosition);

            if (moveSuccessful) {
                final Piece movedPieceForAnimation = pieceToMove; 
                
                if (gameView != null) {
                    gameView.updateView(); 
                    if (isCapture && capturedPieceForAnimation != null && victimOriginalPos != null) {
                        // Tell BoardPanel to keep the victim visually present at its original spot
                        gameView.stagePieceForDeathAnimation(capturedPieceForAnimation, victimOriginalPos);
                    }
                }
                
                this.selectedPiece = null;
                this.selectedCard = null;
                if (gameView != null) {
                    gameView.clearHighlightsAndPossibleMoves(); 
                }

                Runnable finalMessageUpdates = () -> {
                    if(gameView != null) {
                        if (gameState.isGameOver()) {
                            gameView.showGameOver(gameState.getGameStatus());
                        } else {
                            gameView.showMessage("Player " + gameState.getCurrentPlayerId() + "'s turn.");
                        }
                    }
                };

                Runnable afterDeathAnimation = finalMessageUpdates; 

                Runnable afterMoveAndAttackAnimation = () -> {
                    if (isCapture && capturedPieceForAnimation != null) {
                        if(gameView != null) gameView.clearStagedDeathPiece(); // Stop drawing it as idle
                        gameView.animateDeath(capturedPieceForAnimation, afterDeathAnimation);
                    } else {
                        finalMessageUpdates.run(); 
                    }
                };
                
                gameView.animateMove(movedPieceForAnimation, startPosition, targetPosition, isCapture, afterMoveAndAttackAnimation);

            } else { 
                gameView.showMessage("Invalid move. Try again.");
                 gameView.clearPossibleMoves();
                 if (this.selectedPiece != null && this.selectedCard != null) {
                     gameView.showPossibleMoves(gameState.getPossibleMoves(currentPlayer, this.selectedPiece, this.selectedCard));
                 }
            }
        }
    }

    public void handleCardSelection(MoveCard card) {
        if (gameState.isGameOver() || card == null) return;
        if (gameView == null) return;

        Player currentPlayer = gameState.getCurrentPlayer();
        if (currentPlayer == null || gameState.getPlayer(currentPlayer.getId()) == null ) return;


        if (currentPlayer.hasMoveCard(card)) {
            setSelectedCard(card); 

            if (selectedPiece != null) { 
                gameView.showPossibleMoves(
                        gameState.getPossibleMoves(currentPlayer, selectedPiece, selectedCard));
            }
        } else {
            gameView.showMessage("This card is not in your hand or it's not your turn to select it.");
        }
    }
}