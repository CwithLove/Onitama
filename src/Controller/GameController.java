package Controller;

import Boundary.GameView;
import Entity.*; // Sẽ tạo sau
import java.awt.Point;

public class GameController {
    // Attributs
    private GameState gameState;
    private GameView gameView; // Tham chiếu đến View

    // Save selected piece and card for the current turn
    private Piece selectedPiece = null;
    private MoveCard selectedCard = null;

    /* ------------------- */
    /* --- Contructors --- */
    public GameController() {
        this.gameState = new GameState();
    }
    /* ------------------- */


    
    /* --------------- */
    /* --- Setters --- */
    public void setView(GameView gameView) {
        this.gameView = gameView;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setSelectedPiece(Piece selectedPiece) {
        this.selectedPiece = selectedPiece;
    }

    public void setSelectedCard(MoveCard selectedCard) {
        this.selectedCard = selectedCard;
    }
    /* --- --- --- --- --- */



    /* --------------- */
    /* --- Getters --- */
    public GameState getGameState() {
        return gameState;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public MoveCard getSelectedCard() {
        return selectedCard;
    }
    /* --- --- --- --- --- */



    /* ---------------- */
    /* --- GameLoop --- */
    public void startGame() {
        gameState.initializeGameSetup();
        if (gameView != null) {
            gameView.updateView(); // Update the board and cards
            gameView.showMessage("Game started. Player " + gameState.getCurrentPlayerId() + "'s turn.");
        }
    }

    public void resetGame() {
        gameState.resetGame();
        selectedPiece = null;
        selectedCard = null;
        if (gameView != null) {
            gameView.updateView(); // Reset the board and cards
            gameView.showMessage("Game reset. Player " + gameState.getCurrentPlayerId() + "'s turn.");
        }
    }
    /* --- --- --- --- --- */



    /* -------------------- */
    /* --- Handle Input --- */
    // Handle user input for cell clicks and card selections
    public void handleCellClick(int row, int col) {
        if (gameState.isGameOver())
            return;

        Player currentPlayer = gameState.getCurrentPlayer();
        Point clickedPoint = new Point(row, col);

        // Step 1: Select piece
        if (selectedPiece == null) {
            Piece pieceAtClick = gameState.getBoard().getPieceAt(row, col);
            if (pieceAtClick != null && pieceAtClick.isBelongTo(currentPlayer)) {
                selectedPiece = pieceAtClick;
                if (gameView != null) {
                    gameView.highlightSelectedPiece(selectedPiece);
                    // If a card is already selected, show possible moves for the selected piece
                    if (selectedCard != null) {
                        gameView.showPossibleMoves(
                                gameState.getPossibleMoves(currentPlayer, selectedPiece, selectedCard));
                    }
                }
            } else {
                if (gameView != null)
                    gameView.showMessage("Invalid piece selection.");
            }

        // Step 2: Already selected a piece -> select destination (or switch pieces)
        } else {
            Piece pieceAtClick = gameState.getBoard().getPieceAt(row, col);
            if (pieceAtClick != null && pieceAtClick.isBelongTo(currentPlayer)) { // Switching pieces
                selectedPiece = pieceAtClick;
                if (gameView != null) {
                    gameView.highlightSelectedPiece(selectedPiece);
                    if (selectedCard != null) {
                        gameView.showPossibleMoves(
                                gameState.getPossibleMoves(currentPlayer, selectedPiece, selectedCard));
                    } else {
                        gameView.clearPossibleMoves(); // Delete possible moves if no card is selected
                    }
                }
                return;
            }

            // Step 3: Already selected a piece and card -> perform move
            if (selectedCard != null) {
                // Save the previous position for animation
                Point previousPosition = new Point(selectedPiece.getPosX(), selectedPiece.getPosY());

                boolean moveSuccessful = gameState.playTurn(selectedPiece, selectedCard, clickedPoint);

                if (moveSuccessful) {
                    // Need to use final for movedPiece and playedCard
                    final Piece movedPiece = selectedPiece;
                    final MoveCard playedCard = selectedCard;

                    // Reset after a successful move
                    this.selectedPiece = null;
                    this.selectedCard = null;

                    if (gameView != null) {
                        // Make animations
                        // After the move, update the view
                        gameView.animateMove(movedPiece, previousPosition, clickedPoint, () -> {
                            gameView.updateView(); // Update the board and cards
                            if (gameState.isGameOver()) {
                                gameView.showGameOver(gameState.getGameStatus());
                            } else {
                                gameView.showMessage("Player " + gameState.getCurrentPlayerId() + "'s turn.");
                            }
                            // Update player cards after the swap
                            gameView.updatePlayerCards(gameState.getPlayer1());
                            gameView.updatePlayerCards(gameState.getPlayer2());
                            gameView.updateNeutralCard(gameState.getNeutralCardMove());
                        });
                    }
                } else {
                    if (gameView != null) {
                        gameView.showMessage("Invalid move. Try again.");
                        // Don't reset selectedPiece, selectedCard to allow user to try again
                    }
                }
            } else {
                if (gameView != null)
                    gameView.showMessage("Please select a move card first.");
            }
        }
    }

    // Handle user input for card selection
    public void handleCardSelection(MoveCard card) {
        if (gameState.isGameOver() || card == null)
            return;

        Player currentPlayer = gameState.getCurrentPlayer();
        if (currentPlayer.hasMoveCard(card)) {
            selectedCard = card;
            if (gameView != null) {
                gameView.highlightSelectedCard(selectedCard);
                // If a piece is already selected, show possible moves for the selected piece
                if (selectedPiece != null) {
                    gameView.showPossibleMoves(gameState.getPossibleMoves(currentPlayer, selectedPiece, selectedCard));
                }
            }
        } else {
            if (gameView != null)
                gameView.showMessage("This card is not in your hand.");
        }
    }
    /* --- --- --- --- --- */
}