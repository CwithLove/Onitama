package Boundary;

import Controller.GameController;
import Entity.*;
import java.awt.*;
import java.util.ArrayList; 
import javax.swing.*;

public class GameView extends JFrame {
    // Attributes
    private GameController controller;
    private BoardPanel boardPanel;
    private PlayerCardsPanel playerCardsPanelP1;
    private PlayerCardsPanel playerCardsPanelP2;
    private NeutralCardPanel neutralCardPanel;
    private JLabel statusLabel;
    private JButton restartButton;

    /* Constructors */
    public GameView(GameController controller) {
        this.controller = controller;
        initUI();
    }

    /* Initialize UI */
    private void initUI() {
        setTitle("Onitama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        boardPanel = new BoardPanel(controller);
        add(boardPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Welcome to Onitama!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> {
            controller.resetGame();
        });
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusLabel, BorderLayout.CENTER);
        southPanel.add(restartButton, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);

        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel p1Label = new JLabel("Player 1 (Blue)");
        p1Label.setFont(new Font("Arial", Font.BOLD, 14));
        westPanel.add(p1Label);
        playerCardsPanelP1 = new PlayerCardsPanel(controller, 1);
        westPanel.add(playerCardsPanelP1);
        add(westPanel, BorderLayout.WEST);
        
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel p2Label = new JLabel("Player 2 (Red)");
        p2Label.setFont(new Font("Arial", Font.BOLD, 14));
        eastPanel.add(p2Label);
        playerCardsPanelP2 = new PlayerCardsPanel(controller, 2);
        eastPanel.add(playerCardsPanelP2);
        add(eastPanel, BorderLayout.EAST);
        
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
        JLabel neutralLabel = new JLabel("Neutral Card: ");
        neutralLabel.setFont(new Font("Arial", Font.BOLD, 14));
        northPanel.add(neutralLabel);
        neutralCardPanel = new NeutralCardPanel(); 
        northPanel.add(neutralCardPanel);
        add(northPanel, BorderLayout.NORTH);

        pack();
        setMinimumSize(new Dimension(800, 600)); 
        setLocationRelativeTo(null);
    }

    /* Update View Methods */
    public void updateView() {
        GameState gs = controller.getGameState();
        if (gs == null) return;

        boardPanel.updateBoard(gs.getBoard()); 
        
        if (gs.getPlayer1() != null) updatePlayerCards(gs.getPlayer1());
        if (gs.getPlayer2() != null) updatePlayerCards(gs.getPlayer2());
        updateNeutralCard(gs.getNeutralCardMove());

        if (gs.getNeutralCardMove() != null) { 
            Player currentPlayer = gs.getCurrentPlayer();
            if (currentPlayer != null && currentPlayer.getId() == 1) {
                playerCardsPanelP1.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                playerCardsPanelP2.setBorder(BorderFactory.createEmptyBorder(2,2,2,2)); 
            } else if (currentPlayer != null && currentPlayer.getId() == 2) {
                playerCardsPanelP2.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                playerCardsPanelP1.setBorder(BorderFactory.createEmptyBorder(2,2,2,2)); 
            } else { 
                 playerCardsPanelP1.setBorder(BorderFactory.createEmptyBorder(2,2,2,2)); 
                 playerCardsPanelP2.setBorder(BorderFactory.createEmptyBorder(2,2,2,2)); 
            }
        }
        repaint(); 
    }
    
    public void updatePlayerCards(Player player) {
        if (player.getId() == 1) {
            playerCardsPanelP1.updateCards(player.getMoveCards());
        } else {
            playerCardsPanelP2.updateCards(player.getMoveCards());
        }
    }

    public void updateNeutralCard(MoveCard card) {
        neutralCardPanel.updateCard(card);
    }

    public void highlightSelectedPiece(Piece piece) {
        boardPanel.setSelectedPiece(piece);
        boardPanel.repaint();
    }
    
    public void highlightSelectedCard(MoveCard card) {
        if (controller.getGameState() == null || 
            controller.getGameState().getPlayer1() == null || 
            controller.getGameState().getPlayer2() == null) {
            return; 
        }

        boolean cardIsForP1 = controller.getGameState().getPlayer1().hasMoveCard(card);
        boolean cardIsForP2 = controller.getGameState().getPlayer2().hasMoveCard(card);

        if (cardIsForP1 && controller.getGameState().getCurrentPlayerId() == 1) {
            playerCardsPanelP1.setSelectedCard(card);
            playerCardsPanelP2.setSelectedCard(null); 
        } else if (cardIsForP2 && controller.getGameState().getCurrentPlayerId() == 2) {
            playerCardsPanelP2.setSelectedCard(card);
            playerCardsPanelP1.setSelectedCard(null); 
        } else { 
            playerCardsPanelP1.setSelectedCard(null);
            playerCardsPanelP2.setSelectedCard(null);
        }
        playerCardsPanelP1.repaint();
        playerCardsPanelP2.repaint();
    }

    public void showPossibleMoves(ArrayList<Point> moves) {
        boardPanel.setPossibleMoves(moves);
        boardPanel.repaint();
    }
    
    public void clearPossibleMoves() {
        boardPanel.setPossibleMoves(new ArrayList<>());
        boardPanel.repaint();
    }
    
    public void clearHighlightsAndPossibleMoves() {
        boardPanel.setSelectedPiece(null);
        if (playerCardsPanelP1 != null) playerCardsPanelP1.setSelectedCard(null);
        if (playerCardsPanelP2 != null) playerCardsPanelP2.setSelectedCard(null);
        boardPanel.setPossibleMoves(new ArrayList<>());
        
        boardPanel.repaint();
        if (playerCardsPanelP1 != null) playerCardsPanelP1.repaint();
        if (playerCardsPanelP2 != null) playerCardsPanelP2.repaint();
    }

    /* Message Display */
    public void showMessage(String message) {
        statusLabel.setText(message);
    }

    public void showGameOver(String message) {
        JOptionPane.showMessageDialog(this, "Game Over: " + message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("Game Over: " + message + " Click Restart to play again.");
    }

    /* Animation Triggers */
    public void animateMove(Piece piece, Point fromPos, Point toPos, boolean isCapture, Runnable onAnimationComplete) {
        boardPanel.animateMove(piece, fromPos, toPos, isCapture, onAnimationComplete);
    }

    public void animateDeath(Piece piece, Runnable onDeathComplete) {
        boardPanel.animateDeath(piece, onDeathComplete);
    }

    // New methods to delegate to BoardPanel for staging death
    public void stagePieceForDeathAnimation(Piece piece, Point originalPos) {
        boardPanel.setPieceStagedForDeath(piece, originalPos);
    }
    public void clearStagedDeathPiece() {
        boardPanel.clearPieceStagedForDeath();
    }
}