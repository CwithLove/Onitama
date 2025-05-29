package Entity;

public class GameState {   
    // Attributes representing the game state
    private Board board;
    private Player player1;
    private Player player2;

    // Current player whose turn it is
    private int currentPlayerId;
    // Reference to the current player object and piece chosen for the turn
    private Player currentPlayer;
    private Piece currentPiece;

    // Game status (e.g., ongoing, finished)
    private String gameStatus;
    // Game mode (e.g., single-player, multiplayer)
    private String gameMode;
    // Game settings (e.g., difficulty level, time limits)
    private String gameSettings;
    /* ------------------- */



    public void startGame() {
        // Initialize game state and start the game
        System.out.println("Game has started!");
    }
}
