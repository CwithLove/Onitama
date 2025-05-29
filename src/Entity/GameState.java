package Entity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GameState {
    // Attributes representing the game state
    private Board board;
    private Player player1;
    private Player player2;
    private MoveCard neutralCardMove;

    // Current player whose turn it is
    private int currentPlayerId;
    // Reference to the current player object and piece chosen for the turn
    private Piece currentPiece;

    // Game status (e.g., ongoing, finished)
    private String gameStatus;
    // Game mode (e.g., single-player, multiplayer)
    // private String gameMode;
    // Game settings (e.g., difficulty level, time limits)
    // private String gameSettings;
    /* ------------------- */

    /* ------------------ */
    /* --- Constructor --- */
    public GameState() {
        this.board = new Board();
        this.player1 = new Player(1);
        this.player2 = new Player(2);
        this.currentPlayerId = 1; // Start with player 1
        this.gameStatus = "ongoing"; // Initial game status
        // this.gameMode = "multiplayer"; // Default game mode
        // this.gameSettings = "default"; // Default game settings
    }

    // Copy constructor
    public GameState(GameState otherGameState) {
        this.board = new Board(otherGameState.board);
        this.player1 = new Player(otherGameState.player1);
        this.player2 = new Player(otherGameState.player2);
        this.currentPlayerId = otherGameState.getCurrentPlayerId();
        Piece p;
        if ((p = otherGameState.getCurrentPiece()) != null) {
            if (p.isMaster())
                this.currentPiece = new Master((Master) p);
            else if (p.isPawn())
                this.currentPiece = new Pawn((Pawn) p);
        } else {
            this.currentPiece = null;
        }

        this.gameStatus = otherGameState.getGameStatus();
        // this.gameMode = otherGameState.gameMode;
        // this.gameSettings = otherGameState.gameSettings;
    }
    /* --- --- --- --- --- */

    /* -------------- */
    /* --- Getter --- */
    public Board getBoard() {
        return board;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public Piece getCurrentPiece() {
        return currentPiece;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public MoveCard getNeutralCardMove() {
        return this.neutralCardMove;
    }
    /* --- --- --- --- --- */

    /* -------------- */
    /* --- Setter --- */
    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setCurrentPlayerId(int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public void setCurrentPiece(Piece currentPiece) {
        this.currentPiece = currentPiece;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setNeutralCardMove(MoveCard neutralCardMove) {
        this.neutralCardMove = neutralCardMove;
    }
    /* --- --- --- --- --- */

    /* -------------- */
    /* --- Status --- */
    public int checkWinConditions() {
        Player currentPlayer = getCurrentPlayer();
        Player opponentPlayer = getOpponentPlayer();

        if (currentPlayer == null || opponentPlayer == null) {
            System.err.println("GameState: Game is not properly set up");
            return 0;
        }

        // First condition: Capture opponent's Master (Way of the Stone)
        Master opponentMaster = this.board.getMasterForPlayer(opponentPlayer.getId());
        if (opponentMaster == null) {
            this.gameStatus = "Player " + currentPlayer.getId() + " wins by Way of the Stone";
            return currentPlayer.getId();
        }

        // Second Condition: Current player's Master reachs opponents's Temple Arch (Way
        // of the Stream)
        Master currentPlayerMaster = board.getMasterForPlayer(currentPlayer.getId());
        if (currentPlayerMaster != null) {
            Point currentPlayerMasterPos = currentPlayerMaster.getPosition();
            Point opponentTempleArch;

            if (opponentPlayer.getId() == player1.getId()) {
                opponentTempleArch = new Point(2, 0);
            } else {
                opponentTempleArch = new Point(2, 4);
            }

            if (currentPlayerMasterPos.equals(opponentTempleArch)) {
                this.gameStatus = "Player " + currentPlayer.getId() + " wins by Way of the Stream";
                return currentPlayer.getId();
            }
        }

        return 0;
    }

    public boolean isGameOver() {
        return !this.gameStatus.equalsIgnoreCase("ongoing");
    }
    /* --- --- --- --- --- */

    /* ----------------- */
    /* --- Utilities --- */
    public Player getCurrentPlayer() {
        return this.currentPlayerId == this.player1.getId() ? player1 : player2;
    }

    public Player getOpponentPlayer() {
        Player currPlayer = getCurrentPlayer();
        if (currPlayer == null)
            return null;
        if (currPlayer.getId() == this.player1.getId())
            return this.player2;
        else
            return this.player1;
    }

    // Gets all possible moves for the current player based on their pieces and move cards
    public ArrayList<Point> getPossibleMoves(Player player) {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        if (player == null) {
            System.err.println("GameState: Player is null. Cannot get possible moves.");
            return possibleMoves;
        }

        List<Piece> playerPieces = this.board.getPiecesForPlayer(player.getId());
        List<MoveCard> playerCards = player.getMoveCards();

        for (Piece piece : playerPieces) {
            Point piecePosition = piece.getPosition();
            for (MoveCard card : playerCards) {
                for (Point move : card.getMoves()) {
                    Point newPosition;
                    if (player.getId() == 1) {
                        newPosition = new Point(piecePosition.x + move.x, piecePosition.y + move.y);
                    } else {
                        newPosition = new Point(piecePosition.x - move.x, piecePosition.y - move.y);
                    }
                    if (this.board.isValidPosition(newPosition) && 
                        (this.board.getPieceAt(newPosition) == null || !this.board.getPieceAt(newPosition).isBelongTo(player))) {
                        possibleMoves.add(newPosition);
                    }
                }
            }
        }
        return possibleMoves;
    }

    // Overloaded method to get possible moves for the current player using the current piece and move card
    public ArrayList<Point> getPossibleMoves(Player player, Piece piece, MoveCard card) {
        ArrayList<Point> possibleMoves = new ArrayList<>();
        if (player == null || piece == null || card == null) {
            System.err.println("GameState: Player, piece, or card is null. Cannot get possible moves.");
            return possibleMoves;
        }

        Point piecePosition = piece.getPosition();
        for (Point move : card.getMoves()) {
            Point newPosition;
            if (player.getId() == 1) {
                newPosition = new Point(piecePosition.x + move.x, piecePosition.y + move.y);
            } else {
                newPosition = new Point(piecePosition.x - move.x, piecePosition.y - move.y);
            }
            if (this.board.isValidPosition(newPosition) && 
                (this.board.getPieceAt(newPosition) == null || !this.board.getPieceAt(newPosition).isBelongTo(player))) {
                possibleMoves.add(newPosition);
            }
        }
        return possibleMoves;
    }


    /* --- --- --- --- --- */


    /* ------------------- */
    /* --- Game Engine --- */
    // Initializes the game setup by shuffling the deck and distributing cards to
    // players
    public void initializeGameSetup() {
        MoveCard[] deckCards = Deck.getDeckCards();
        if (deckCards.length < 5) {
            throw new IllegalStateException("Not enough cards to start the game");
        }

        // Shuffle the card
        List<MoveCard> shuffleDeck = new ArrayList<>(Arrays.asList(deckCards));
        Collections.shuffle(shuffleDeck);

        // Distribute the cards: 2 for p1, 2 for p2, 1 neutral
        player1.setMoveCards(new ArrayList<>(List.of(shuffleDeck.get(0), shuffleDeck.get(1))));

        player2.setMoveCards(new ArrayList<>(List.of(shuffleDeck.get(2), shuffleDeck.get(3))));

        this.setNeutralCardMove(shuffleDeck.get(4));

        // Base on the neutral card, set the starting player
        if (this.getNeutralCardMove().getStarting() == 1) {
            this.setCurrentPlayerId(1);
        } else {
            this.setCurrentPlayerId(2);
        }
        System.out.println("GameState: Starting player set to Player " + this.getCurrentPlayerId());

        this.gameStatus = "ongoing"; // Reset game status to ongoing
        this.currentPiece = null; // Reset current piece

        System.out.println("GameState: Game setup initialized with players and move cards.");
        System.out.println(player1);
        System.out.println(player2);
        System.out.println("GameState: Neutral Move Card: " + this.getNeutralCardMove());
        System.out.println("GameState: Current Player ID: " + this.getCurrentPlayerId());

    }

    // Plays a turn by moving a piece and applying the move card
    public boolean playTurn(Piece pieceToMove, MoveCard cardPlayed, Point targetPosition) {
        if (isGameOver()) {
            System.err.println("GameState: Game is already over. Cannot play turn.");
            return false;
        }

        Player player = getCurrentPlayer();
        if (player == null || pieceToMove == null || cardPlayed == null || targetPosition == null) {
            System.err.println("GameState: Invalid turn parameters.");
            return false;
        }

        // Check if the piece belongs to the current player
        if (!pieceToMove.isBelongTo(player)) {
            System.err.println("GameState: Piece " + pieceToMove + " does not belong to the current player " + player.getId());
            return false;
        }

        // Verify if the move card is belong to the current player
        if (!player.hasMoveCard(cardPlayed)) {
            System.err.println("GameState: Move card does not belong to the current player.");
            return false;
        }

        // Validate the move using (logic from MoveCard)
        Point piecePosition = pieceToMove.getPosition();
        boolean patternMatched = false;
        Point newPosition = null;
        for (Point move : cardPlayed.getMoves()) {
            if (this.currentPlayerId == 1) {
                newPosition = new Point(piecePosition.x + move.x, piecePosition.y + move.y);
            } else {
                newPosition = new Point(piecePosition.x - move.x, piecePosition.y - move.y);
            }
            if (newPosition.equals(targetPosition)) {
                patternMatched = true;
                break; // Found a valid move pattern
            }
        }

        if (!patternMatched) {
            System.err.println("GameState: Invalid move pattern for the piece.");
            return false;
        }

        if (!this.board.isValidPosition(targetPosition)) {
            System.err.println("GameState: Target position is not valid on the board.");
            return false;
        }

        Piece pieceAtTarget = this.board.getPieceAt(targetPosition);
        if (pieceAtTarget != null && pieceAtTarget.isBelongTo(player)) {
            return false;
        }

        // If everything is valid, proceed with the move
        // Move the piece to the target position
        if (pieceAtTarget != null) {
            System.out.println("GameState: Piece captured: " + pieceAtTarget + " at " + targetPosition);
        }

        // Process the move
        this.board.setPieceAt(piecePosition, null); // Remove piece from old position
        this.board.setPieceAt(targetPosition, pieceToMove); // Place piece at new position
        pieceToMove.setPosition(targetPosition); // Update piece's position

        // Exchange the move card
        if (!player.exchangeMoveCard(cardPlayed, this.getNeutralCardMove())) {
            System.err.println("GameState: Failed to exchange move card.");
            return false;
        }
        this.setNeutralCardMove(cardPlayed); // Set the neutral card to the played card

        // Verify win conditions after the move
        int winnerId = checkWinConditions();
        if (winnerId != 0) {
            return true;
        }

        // Switch to the next player
        switchPlayerTurn();
        return true;
    }

    // Switches the turn to the next player
    public void switchPlayerTurn() {
        if (this.currentPlayerId == this.player1.getId()) {
            this.currentPlayerId = this.player2.getId();
        } else {
            this.currentPlayerId = this.player1.getId();
        }
        this.currentPiece = null; // Reset current piece for the new turn
        System.out.println("GameState: Switched to Player " + this.currentPlayerId + "'s turn.");
    }
    /* --- --- --- --- --- */

    /* ----------------------- */
    /* --- Start Game Text --- */
    public void startGame() {
        // Initialize game setup
        System.out.println("Game has started!");
        initializeGameSetup();

        Scanner scanner = new Scanner(System.in);

        // Main game loop
        while (!isGameOver()) {
            Player currentPlayer = getCurrentPlayer();
            if (currentPlayer == null) { // Should not happen if initialized correctly
                System.err.println("Error: Current player is not set. Exiting game.");
                break;
            }

            System.out.println("\n------------------------------------------");
            System.out.println("Current Board State:");
            System.out.println(this.board.toString());
            System.out.println("------------------------------------------");
            System.out.println("It's Player " + currentPlayer.getId() + "'s turn.");
            System.out.println("Player " + currentPlayer.getId() + " cards: ");
            List<MoveCard> playerCards = currentPlayer.getMoveCards();
            for (int i = 0; i < playerCards.size(); i++) {
                System.out.println((i + 1) + ". " + playerCards.get(i));
                // Optionally print card moves: System.out.println(" Moves: " +
                // playerCards.get(i).getMoves());
            }
            System.out
                    .println("Neutral Card: " + this.neutralCardMove);

            ArrayList<Point> possibleMoves = getPossibleMoves(currentPlayer);
            if (possibleMoves.isEmpty()) {
                System.out.println("No possible moves for Player " + currentPlayer.getId() + ". Skipping turn.");
                switchPlayerTurn();
                continue; // Skip to next player's turn
            }

            System.out.println("------------------------------------------");

            boolean turnSuccessful = false;
            while (!turnSuccessful) {
                try {
                    // 1. Get Piece to Move from Player
                    System.out.print("Select piece to move (enter current row and column, e.g., '4 2'): ");
                    int pieceRow = scanner.nextInt();
                    int pieceCol = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (!board.isValidPosition(pieceRow, pieceCol)) {
                        System.out.println("Invalid position. Please enter row and column within board limits.");
                        continue;
                    }

                    Piece selectedPiece = board.getPieceAt(pieceRow, pieceCol);
                    if (selectedPiece == null) {
                        System.out.println("No piece at that position. Try again.");
                        continue;
                    }
                    System.out.println("=> Selected piece: " + selectedPiece); 

                    if (!selectedPiece.isBelongTo(currentPlayer)) {
                        System.out.println("That piece does not belong to you. Try again.");
                        continue;
                    }
                    this.setCurrentPiece(selectedPiece); // Set the piece the player intends to move

                    // 2. Get Card to Play
                    System.out.print("Select card to play (enter 1 or 2 from your hand): ");
                    int cardChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (cardChoice < 1 || cardChoice > playerCards.size()) {
                        System.out.println("Invalid card choice. Try again.");
                        continue;
                    }
                    MoveCard selectedCard = playerCards.get(cardChoice - 1);
                    System.out.println("Selected card: " + selectedCard.getCardName());
                    System.out.print("Possible moves for selected piece using " + selectedCard.getCardName() + ":");
                    // Get possible moves for the selected piece with the selected card
                    ArrayList<Point> possibleMovesForCard = getPossibleMoves(currentPlayer, selectedPiece, selectedCard); 

                    if (possibleMovesForCard.isEmpty()) {
                        System.out.println("  No valid moves available for this piece with the selected card.");
                        continue; // Ask for piece and card again
                    } else {
                        for (Point move : possibleMovesForCard) {
                            System.out.print("(" + move.x + "," + move.y + ") ");
                        }
                        System.out.println(); // New line after printing all possible moves
                    }

                    this.board.printPossibleMoves(possibleMovesForCard); // Print the board with possible moves highlighted

                    // 3. Get Target Position
                    System.out.print("Enter target row and column to move to (e.g., '3 2'): ");
                    int targetRow = scanner.nextInt();
                    int targetCol = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (targetRow == 9 && targetCol == 9) {
                        System.out.println("Cancelling turn. Please select a different piece or card.");
                        continue;
                    }
                    Point targetPosition = new Point(targetRow, targetCol); // Assuming Point(row, col)

                    // 4. Attempt to Play Turn
                    System.out.println(
                            "Attempting to move " + selectedPiece.getType() + " from (" + pieceRow + "," + pieceCol
                                    + ") to (" + targetRow + "," + targetCol + ") using " + selectedCard.getCardName());
                    turnSuccessful = playTurn(this.currentPiece, selectedCard, targetPosition);

                    if (!turnSuccessful) {
                        System.out.println(
                                "Move was not successful. Please check the rules or your input and try again.");
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Invalid input format. Please enter numbers as required. Try again.");
                    scanner.nextLine(); // Clear the invalid input
                } catch (Exception e) {
                    System.out.println(
                            "An unexpected error occurred: " + e.getMessage() + ". Please try your turn again.");
                    // It might be good to log e.printStackTrace() for debugging
                }
            } // End of inner while loop for current player's turn
        } // End of while(!isGameOver()) loop

        // Game Over
        System.out.println("\n==========================================");
        System.out.println("GAME OVER!");
        System.out.println(getGameStatus()); // This should now contain the win message
        System.out.println("Final Board State:");
        System.out.println(this.board.toString());
        System.out.println("==========================================");

        scanner.close();
    }
    /* --- --- --- --- --- */

    @Override
    public String toString() {
        return "GameState{" +
                "board=" + board +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", currentPlayerId=" + currentPlayerId +
                ", currentPiece=" + currentPiece +
                ", gameStatus='" + gameStatus + '\'' +
                ", neutralCardMove=" + neutralCardMove +
                '}';
    }
}
