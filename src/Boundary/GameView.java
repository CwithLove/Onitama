package Boundary;

import Controller.GameController;
import Entity.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class GameView extends JFrame {
    // Atributs
    // Controller reference
    private GameController controller;

    // Selected piece and card for the current turn
    private BoardPanel boardPanel;
    private PlayerCardsPanel playerCardsPanelP1;
    private PlayerCardsPanel playerCardsPanelP2;
    private NeutralCardPanel neutralCardPanel;

    // Labels and buttons
    private JLabel statusLabel;
    private JButton restartButton;


    /* -------------------- */
    /* --- Constructors --- */
    public GameView(GameController controller) {
        this.controller = controller;
        initUI();
    }
    /* --- --- --- --- --- */


    /* ------------------- */
    /* --- Initialize --- */
    private void initUI() {
        setTitle("Onitama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // main layout

        // Board Panel (Center)
        boardPanel = new BoardPanel(controller);
        add(boardPanel, BorderLayout.CENTER);

        // Status Label (South)
        statusLabel = new JLabel("Welcome to Onitama!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Restart Button
        restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> {
            controller.resetGame(); // Reset the game state
            controller.startGame(); // Re-initialize and start
            controller.setSelectedCard(null); // Clear selected card
            controller.setSelectedPiece(null); // Clear selected piece
            clearHighlightsAndPossibleMoves();
        });
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusLabel, BorderLayout.CENTER);
        southPanel.add(restartButton, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);


        // Player 1 Cards (West)
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel p1Label = new JLabel("Player 1 (Blue)");
        p1Label.setFont(new Font("Arial", Font.BOLD, 14));
        westPanel.add(p1Label);
        playerCardsPanelP1 = new PlayerCardsPanel(controller, 1); // Player 1
        westPanel.add(playerCardsPanelP1);
        add(westPanel, BorderLayout.WEST);

        
        // Player 2 Cards (East)
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel p2Label = new JLabel("Player 2 (Red)");
        p2Label.setFont(new Font("Arial", Font.BOLD, 14));
        eastPanel.add(p2Label);
        playerCardsPanelP2 = new PlayerCardsPanel(controller, 2); // Player 2
        eastPanel.add(playerCardsPanelP2);
        add(eastPanel, BorderLayout.EAST);
        
        
        // Neutral Card (North)
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
        JLabel neutralLabel = new JLabel("Neutral Card: ");
        neutralLabel.setFont(new Font("Arial", Font.BOLD, 14));
        northPanel.add(neutralLabel);
        neutralCardPanel = new NeutralCardPanel();
        northPanel.add(neutralCardPanel);
        add(northPanel, BorderLayout.NORTH);


        pack();
        setMinimumSize(getPreferredSize()); // Ensure minimum size
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }
    /* --- --- --- --- --- */


    /* ------------------- */
    /* --- Update View --- */
    // Update the view based on the current game state
    public void updateView() {
        GameState gs = controller.getGameState();
        boardPanel.updateBoard(gs.getBoard());
        updatePlayerCards(gs.getPlayer1());
        updatePlayerCards(gs.getPlayer2());
        updateNeutralCard(gs.getNeutralCardMove());

        // Update the current player based on the neutral card color
        if (gs.getNeutralCardMove() != null) {
            Color turnColor = gs.getNeutralCardMove().getStarting() == gs.getCurrentPlayerId() ? Color.BLACK : Color.GRAY;
            // This logic might need refinement based on whose turn it actually is vs. card color.
            // GameState.getCurrentPlayerId() is the source of truth.
            // You might want to highlight the active player's panel.
             if (gs.getCurrentPlayerId() == 1) {
                playerCardsPanelP1.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                playerCardsPanelP2.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
            } else {
                playerCardsPanelP2.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                playerCardsPanelP1.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
            }
        }
        
        // Clear selections and highlights as turn might have changed
        clearHighlightsAndPossibleMoves();
        repaint();
    }
    
    // Update player cards based on the current game state
    public void updatePlayerCards(Player player) {
        if (player.getId() == 1) {
            playerCardsPanelP1.updateCards(player.getMoveCards());
        } else {
            playerCardsPanelP2.updateCards(player.getMoveCards());
        }
    }


    // Update the neutral card panel with the current neutral card
    public void updateNeutralCard(MoveCard card) {
        neutralCardPanel.updateCard(card);
    }

    // Highlight selected piece and card for the current turn
    public void highlightSelectedPiece(Piece piece) {
        boardPanel.setSelectedPiece(piece);
        boardPanel.repaint();
    }
    
    // Highlight selected card for the current turn
    public void highlightSelectedCard(MoveCard card) {
        // Player 1
        boolean p1Selected = false;
        if (controller.getGameState().getPlayer1().hasMoveCard(card)) {
           playerCardsPanelP1.setSelectedCard(card);
           p1Selected = true;
        } else {
           playerCardsPanelP1.setSelectedCard(null); // Clear selection from P1 if card is not theirs
        }
        // Player 2
        if (!p1Selected && controller.getGameState().getPlayer2().hasMoveCard(card)) {
            playerCardsPanelP2.setSelectedCard(card);
        } else {
            playerCardsPanelP2.setSelectedCard(null); // Clear selection from P2 if card is not theirs (or P1 selected it)
        }
        // Repaint panels if they have internal state for selection
        playerCardsPanelP1.repaint();
        playerCardsPanelP2.repaint();
    }

    // Show possible moves for the selected piece
    public void showPossibleMoves(ArrayList<Point> moves) {
        boardPanel.setPossibleMoves(moves);
        boardPanel.repaint();
    }
    
    // Clear all highlights and possible moves
    public void clearPossibleMoves() {
        boardPanel.setPossibleMoves(new ArrayList<>()); // Empty list
        boardPanel.repaint();
    }
    
    // Clear all highlights and possible moves, including selected piece and cards
    public void clearHighlightsAndPossibleMoves() {
        boardPanel.setSelectedPiece(null);
        if (controller.getGameState().getPlayer1() != null) playerCardsPanelP1.setSelectedCard(null);
        if (controller.getGameState().getPlayer2() != null) playerCardsPanelP2.setSelectedCard(null);
        boardPanel.setPossibleMoves(new ArrayList<>());
        boardPanel.repaint();
        if (playerCardsPanelP1 != null) playerCardsPanelP1.repaint();
        if (playerCardsPanelP2 != null) playerCardsPanelP2.repaint();
    }
    /* --- --- --- --- --- */



    /* --------------- */
    /* --- Message --- */
    // Show a message in the status label
    public void showMessage(String message) {
        statusLabel.setText(message);
    }

    // Show a game over message
    public void showGameOver(String message) {
        JOptionPane.showMessageDialog(this, "Game Over: " + message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("Game Over: " + message + " Click Restart to play again.");
    }
    /* --- --- --- --- */



    /* ------------------------ */
    /* --- Update Animation --- */
    // Yêu cầu BoardPanel thực hiện animation
    public void animateMove(Piece piece, Point fromPos, Point toPos, Runnable onAnimationComplete) {
        boardPanel.animateMove(piece, fromPos, toPos, onAnimationComplete);
    }
    /* --- --- --- --- --- */
}