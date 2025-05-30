import Boundary.GameView;
import Controller.GameController;
import Entity.GameState;
import javax.swing.SwingUtilities;

public class Onitama {
    private static String gameMode = "GUI"; // or "GUI"
    
    
    public static void main(String[] args) {
        /* ------------------------- */
        /* --- Terminal GamePlay --- */
        if (gameMode.equals("Terminal")) {
            // Initialize the terminal game view
            // GameView gameView = new GameView();
            // gameView.startGame();
            GameState gameState = new GameState();
            gameState.startGame();
            return;
        }
        /* --- --- --- --- --- */



        /* ------------------- */
        /* -- GUI GamePlay --- */
        if (gameMode.equals("GUI")) {
            SwingUtilities.invokeLater(() -> {   
                GameController controller = new GameController();
                GameView gameView = new GameView(controller);
                controller.setView(gameView);
                gameView.setVisible(true);
                controller.startGame(); // Start the game
            });
        }
        /* --- --- --- --- --- */
    }
}