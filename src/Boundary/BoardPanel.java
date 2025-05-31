package Boundary;

import Controller.GameController;
import Entity.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class BoardPanel extends JPanel {
    private GameController controller;
    private Board currentBoard;
    
    // Dynamic dimension variables
    private int dynamicCellSize;
    private int dynamicPieceSize;
    private int dynamicOffsetX; // For centering the board horizontally
    private int dynamicOffsetY; // For centering the board vertically

    private Piece selectedPiece;
    private List<Point> possibleMoves;

    // Animation variables
    private Piece pieceToAnimate;
    private Point animationStartScreenPos;
    private Point animationEndScreenPos;
    private Point animationCurrentScreenPos;
    private Timer animationTimer;
    private Runnable onAnimationCompleteCallback;
    private final int ANIMATION_DELAY = 15;
    private final int ANIMATION_STEPS = 20;

    /* --- Constructors --- */
    public BoardPanel(GameController controller) {
        this.controller = controller;
        this.possibleMoves = new ArrayList<>();
        // Removed setPreferredSize from here, will use getPreferredSize() override

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (animationTimer != null && animationTimer.isRunning()) return;
                if (currentBoard == null) return;

                // Use dynamic dimensions for click handling
                int boardPixelX = e.getX() - dynamicOffsetX;
                int boardPixelY = e.getY() - dynamicOffsetY;

                int col = boardPixelX / dynamicCellSize;
                int row = boardPixelY / dynamicCellSize;

                // Check if click is within the actual board boundaries
                if (boardPixelX >= 0 && boardPixelX < currentBoard.getColumns() * dynamicCellSize &&
                    boardPixelY >= 0 && boardPixelY < currentBoard.getRows() * dynamicCellSize) {
                    if (controller.getGameState().getBoard().isValidPosition(row, col)) {
                        controller.handleCellClick(row, col);
                    }
                }
            }
        });
    }
    /* --- --- --- --- --- */



    /* ------------------- */
    /* --- Update View --- */
    /**
     * Recalculates cell size, piece size, and offsets based on current panel dimensions and board configuration.
     * This should be called before any painting or coordinate calculations that depend on panel size.
     */
    private void updateDynamicDimensions() {
        if (currentBoard == null || getWidth() == 0 || getHeight() == 0) {
            // Default values if board isn't set or panel isn't visible yet
            this.dynamicCellSize = 60; 
            this.dynamicPieceSize = this.dynamicCellSize * 2 / 3;
            this.dynamicOffsetX = 0;
            this.dynamicOffsetY = 0;
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int numCols = currentBoard.getColumns();
        int numRows = currentBoard.getRows();

        // Calculate cell size to fit the board within the panel while maintaining square cells
        int cellSizeBasedOnWidth = panelWidth / numCols;
        int cellSizeBasedOnHeight = panelHeight / numRows;
        this.dynamicCellSize = Math.min(cellSizeBasedOnWidth, cellSizeBasedOnHeight);
        
        // Ensure minimum cell size if desired
        // this.dynamicCellSize = Math.max(this.dynamicCellSize, 30); // Example minimum

        this.dynamicPieceSize = Math.max(10, this.dynamicCellSize * 2 / 3); // Ensure piece size is reasonable

        // Calculate offsets to center the board
        int boardPixelWidth = numCols * this.dynamicCellSize;
        int boardPixelHeight = numRows * this.dynamicCellSize;

        this.dynamicOffsetX = (panelWidth - boardPixelWidth) / 2;
        this.dynamicOffsetY = (panelHeight - boardPixelHeight) / 2;
    }

    @Override
    public Dimension getPreferredSize() {
        // Provide a preferred size for layout managers (e.g., for JFrame.pack())
        int defaultCellSize = 60; 
        if (currentBoard != null) {
            return new Dimension(currentBoard.getColumns() * defaultCellSize, currentBoard.getRows() * defaultCellSize);
        }
        // Fallback if board is not yet initialized (e.g. 5x5 default)
        return new Dimension(5 * defaultCellSize, 5 * defaultCellSize); 
    }

    public void updateBoard(Board board) {
        this.currentBoard = board;
        // If board dimensions change, preferred size might change, so revalidate.
        revalidate(); 
        repaint();
    }
    /* --- --- --- --- --- */
    

    /* --------------- */
    /* --- Setters --- */
    public void setSelectedPiece(Piece piece) {
        this.selectedPiece = piece;
    }

    public void setPossibleMoves(List<Point> moves) {
        this.possibleMoves = (moves != null) ? moves : new ArrayList<>();
    }
    /* --- --- --- --- --- */



    /* ----------------- */
    /* --- Animation --- */
    public void animateMove(Piece piece, Point boardFromPos, Point boardToPos, Runnable onAnimationComplete) {
        updateDynamicDimensions(); // Ensure dimensions are current before calculating animation path

        this.pieceToAnimate = piece;
        // Convert board logical coordinates (row, col) to screen pixel coordinates for animation
        // boardFromPos.x is row, boardFromPos.y is col
        this.animationStartScreenPos = new Point(
            dynamicOffsetX + boardFromPos.y * dynamicCellSize + dynamicCellSize / 2,
            dynamicOffsetY + boardFromPos.x * dynamicCellSize + dynamicCellSize / 2
        );
        this.animationEndScreenPos = new Point(
            dynamicOffsetX + boardToPos.y * dynamicCellSize + dynamicCellSize / 2,
            dynamicOffsetY + boardToPos.x * dynamicCellSize + dynamicCellSize / 2
        );
        this.animationCurrentScreenPos = new Point(animationStartScreenPos.x, animationStartScreenPos.y);
        this.onAnimationCompleteCallback = onAnimationComplete;

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        final double dx = (double)(animationEndScreenPos.x - animationStartScreenPos.x) / ANIMATION_STEPS;
        final double dy = (double)(animationEndScreenPos.y - animationStartScreenPos.y) / ANIMATION_STEPS;
        final int[] currentStep = {0};

        animationTimer = new Timer(ANIMATION_DELAY, e -> {
            currentStep[0]++;
            if (currentStep[0] >= ANIMATION_STEPS) {
                animationCurrentScreenPos.x = animationEndScreenPos.x;
                animationCurrentScreenPos.y = animationEndScreenPos.y;
                ((Timer) e.getSource()).stop();
                pieceToAnimate = null; 
                repaint(); 
                if (onAnimationCompleteCallback != null) {
                    onAnimationCompleteCallback.run();
                }
            } else {
                animationCurrentScreenPos.x = (int)(animationStartScreenPos.x + dx * currentStep[0]);
                animationCurrentScreenPos.y = (int)(animationStartScreenPos.y + dy * currentStep[0]);
                repaint();
            }
        });
        animationTimer.start();
        repaint(); 
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        updateDynamicDimensions(); // Recalculate dimensions every paint, crucial for responsiveness

        if (currentBoard == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background (optional, if panel is opaque and not transparent)
        g2d.setColor(getBackground()); // Or any color you want for areas outside the board
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw cells, applying offsets and using dynamic cell size
        for (int row = 0; row < currentBoard.getRows(); row++) {
            for (int col = 0; col < currentBoard.getColumns(); col++) {
                int cellX = dynamicOffsetX + col * dynamicCellSize;
                int cellY = dynamicOffsetY + row * dynamicCellSize;
                if ((row + col) % 2 == 0) {
                    g2d.setColor(new Color(209, 192, 147)); 
                } else {
                    g2d.setColor(new Color(165, 131, 82)); 
                }
                g2d.fillRect(cellX, cellY, dynamicCellSize, dynamicCellSize);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(cellX, cellY, dynamicCellSize, dynamicCellSize);
            }
        }
        
        // Draw Temple Arches
        Point p1Temple = new Point(2,0); 
        g2d.setColor(new Color(0,0,255, 80)); 
        g2d.fillRect(dynamicOffsetX + p1Temple.y * dynamicCellSize, 
                     dynamicOffsetY + p1Temple.x * dynamicCellSize, 
                     dynamicCellSize, dynamicCellSize);

        Point p2Temple = new Point(2, currentBoard.getColumns() -1); 
        g2d.setColor(new Color(255,0,0, 80)); 
        g2d.fillRect(dynamicOffsetX + p2Temple.y * dynamicCellSize, 
                     dynamicOffsetY + p2Temple.x * dynamicCellSize, 
                     dynamicCellSize, dynamicCellSize);

        // Highlight selected piece's cell
        if (selectedPiece != null) {
            g2d.setColor(new Color(0, 255, 0, 100)); 
            g2d.fillRect(dynamicOffsetX + selectedPiece.getPosY() * dynamicCellSize, 
                         dynamicOffsetY + selectedPiece.getPosX() * dynamicCellSize, 
                         dynamicCellSize, dynamicCellSize);
        }
        
        // Highlight possible moves
        g2d.setColor(new Color(255, 255, 0, 100)); 
        for (Point move : possibleMoves) {
            g2d.fillRect(dynamicOffsetX + move.y * dynamicCellSize, 
                         dynamicOffsetY + move.x * dynamicCellSize, 
                         dynamicCellSize, dynamicCellSize);
        }

        // Draw pieces
        for (int row = 0; row < currentBoard.getRows(); row++) {
            for (int col = 0; col < currentBoard.getColumns(); col++) {
                Piece p = currentBoard.getPieceAt(row, col);
                if (p != null) {
                    if (p.equals(pieceToAnimate) && animationTimer != null && animationTimer.isRunning()) {
                        continue; // Will be drawn by animation logic
                    }
                    drawPiece(g2d, p, 
                              dynamicOffsetX + col * dynamicCellSize + dynamicCellSize / 2, 
                              dynamicOffsetY + row * dynamicCellSize + dynamicCellSize / 2);
                }
            }
        }
        
        // Draw animating piece at its current interpolated position
        if (pieceToAnimate != null && animationTimer != null && animationTimer.isRunning()) {
            drawPiece(g2d, pieceToAnimate, animationCurrentScreenPos.x, animationCurrentScreenPos.y);
        }
    }

    private void drawPiece(Graphics2D g2d, Piece piece, int centerX, int centerY) {
        if (piece == null) return;

        Color pieceColor = (piece.getPlayerId() == 1) ? new Color(0, 0, 200) : new Color(200, 0, 0);
        Color outlineColor = Color.BLACK;
        
        g2d.setColor(pieceColor);
        if (piece.isMaster()) {
            g2d.fillOval(centerX - dynamicPieceSize / 2, centerY - dynamicPieceSize / 2, dynamicPieceSize, dynamicPieceSize);
            g2d.setColor(outlineColor);
            g2d.drawOval(centerX - dynamicPieceSize / 2, centerY - dynamicPieceSize / 2, dynamicPieceSize, dynamicPieceSize);
            g2d.setColor(Color.WHITE);
            // Adjust font size based on piece size for better scaling (optional)
            int fontSize = Math.max(8, dynamicPieceSize / 3);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString("M", centerX - fm.stringWidth("M") / 2, centerY + fm.getAscent() / 2 - fm.getDescent()/2);
        } else {
            int pawnDrawSize = Math.max(8, dynamicPieceSize - 5); // Ensure pawn is slightly smaller or at least visible
            g2d.fillRect(centerX - pawnDrawSize / 2, centerY - pawnDrawSize / 2, pawnDrawSize, pawnDrawSize);
            g2d.setColor(outlineColor);
            g2d.drawRect(centerX - pawnDrawSize / 2, centerY - pawnDrawSize / 2, pawnDrawSize, pawnDrawSize);
            g2d.setColor(Color.WHITE);
            int fontSize = Math.max(8, pawnDrawSize / 3);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString("P", centerX - fm.stringWidth("P") / 2, centerY + fm.getAscent() / 2 - fm.getDescent()/2);
        }
    }
    /* --- --- --- --- --- */
}