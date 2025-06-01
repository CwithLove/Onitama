package Boundary;

import Controller.GameController;
import Entity.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // Animation pawn
    private Map<String, List<Image>> pawn1IdleAnimation;
    private Map<String, List<Image>> pawn1MovementAnimation;
    private Map<String, List<Image>> pawn1AttackAnimation;
    private Map<String, List<Image>> pawn1DeathAnimation;

    private Map<String, List<Image>> pawn2IdleAnimation;
    private Map<String, List<Image>> pawn2MovementAnimation;
    private Map<String, List<Image>> pawn2AttackAnimation;
    private Map<String, List<Image>> pawn2DeathAnimation;

    // Animation state
    private Map<Piece, AnimationState> pieceAnimationStates;

    private class AnimationState {
        String currentAnimationType; // "idle", "movement", "attack", "death"
        int currentFrameIndex;
        long lastFrameTime; // Timestamp of the last frame change
        boolean isAnimatingMovement; // Whether the piece is currently animating
        boolean isAnimatingDeath; // Whether the piece is currently animating death
        Point animationStartPos; // Start position for movement animation
        Point animationEndPos; // End position for movement animation
        Point animationCurrentScreenPos; // Current interpolated screen position for animation
        Runnable onAnimationCompleteCallback; // Callback when animation finishes
    }

    /* --- Constructors --- */
    public BoardPanel(GameController controller) {
        this.controller = controller;
        this.possibleMoves = new ArrayList<>();
        // Removed setPreferredSize from here, will use getPreferredSize() override

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (animationTimer != null && animationTimer.isRunning())
                    return;
                if (currentBoard == null)
                    return;

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

        pieceAnimationStates = new java.util.HashMap<>();
        loadAnimationAssets();
    }
    /* --- --- --- --- --- */

    /* ------------------- */
    /* --- Load Assets --- */
    private void loadAnimationAssets() {
        pawn1IdleAnimation = loadFrames("Pawn/Pawn_1/Idle", "sprite_", 0, 6); // sprite_0.png -> sprite_5.png
        pawn1MovementAnimation = loadFrames("Pawn/Pawn_1/Movement", "sprite_", 0, 10); // sprite_00.png -> sprite_09.png
        pawn1AttackAnimation = loadFrames("Pawn/Pawn_1/Attack", "sprite_", 0, 9);
        pawn1DeathAnimation = loadFrames("Pawn/Pawn_1/Death", "sprite_", 0, 17);

        pawn2IdleAnimation = loadFrames("Pawn/Pawn_2/Idle", "sprite_", 0, 6); // Giả định có folder Pawn_2
        pawn2MovementAnimation = loadFrames("Pawn/Pawn_2/Movement", "sprite_", 0, 10);
        pawn2AttackAnimation = loadFrames("Pawn/Pawn_2/Attack", "sprite_", 0, 15);
        pawn2DeathAnimation = loadFrames("Pawn/Pawn_2/Death", "sprite_", 0, 15);
    }

    private Map<String, List<Image>> loadFrames(String path, String prefix, int startIdx, int endIdx) {
        Map<String, List<Image>> animations = new HashMap<>();
        List<Image> frames = new ArrayList<>();
        String animationName = path.substring(path.lastIndexOf("/") + 1); // Extract "Idle", "Movement", etc.

        for (int i = startIdx; i < endIdx; i++) {
            String fileName;
            fileName = prefix + String.format("%02d", i) + ".png"; // For sprite_00.png

            try {
                // Đảm bảo đường dẫn chính xác: /res/Pawn/Pawn_1/Idle/sprite_0.png
                Image img = ResourceLoader.loadImage("res/" + path + "/" + fileName);
                frames.add(img);
            } catch (Exception e) {
                System.err.println("Error loading image: /res/" + path + "/" + fileName + " - " + e.getMessage());
                return null; 
            }
        }
        animations.put(animationName.toLowerCase(), frames); 
        return animations;
    }
    /* --- --- --- --- --- */



    /* ------------------- */
    /* --- Update View --- */
    /**
     * Recalculates cell size, piece size, and offsets based on current panel
     * dimensions and board configuration.
     * This should be called before any painting or coordinate calculations that
     * depend on panel size.
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

        // Calculate cell size to fit the board within the panel while maintaining
        // square cells
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
        updateDynamicDimensions();

        this.pieceToAnimate = piece; // Piece that is currently moving
        this.onAnimationCompleteCallback = onAnimationComplete;

        // Update animation state for the piece
        AnimationState state = pieceAnimationStates.getOrDefault(piece, new AnimationState());
        state.currentAnimationType = "movement"; // Set animation type
        state.currentFrameIndex = 0;
        state.lastFrameTime = System.currentTimeMillis();
        state.isAnimatingMovement = true; // Enable movement flag
        state.animationStartPos = new Point(
                dynamicOffsetX + boardFromPos.y * dynamicCellSize + dynamicCellSize / 2,
                dynamicOffsetY + boardFromPos.x * dynamicCellSize + dynamicCellSize / 2);
        state.animationEndPos = new Point(
                dynamicOffsetX + boardToPos.y * dynamicCellSize + dynamicCellSize / 2,
                dynamicOffsetY + boardToPos.x * dynamicCellSize + dynamicCellSize / 2);
        pieceAnimationStates.put(piece, state); // Save the state

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        final int[] currentStep = { 0 };
        final int TOTAL_ANIM_STEPS = 20; // Total interpolation steps for the position
        final int FRAME_DURATION_MS = 60; // Duration of each animation frame (e.g., 1000ms / 10fps = 100ms)

        animationTimer = new Timer(ANIMATION_DELAY, e -> { // ANIMATION_DELAY ~ 15ms for UI redraw
            AnimationState animState = pieceAnimationStates.get(piece);
            if (animState == null) { // Should not happen
                ((Timer) e.getSource()).stop();
                return;
            }

            // Update animation frame
            if (System.currentTimeMillis() - animState.lastFrameTime > FRAME_DURATION_MS) {
                animState.currentFrameIndex++;
                List<Image> frames = (piece.getPlayerId() == 1 ? pawn1MovementAnimation : pawn2MovementAnimation)
                        .get("movement"); // Get list of movement frames

                if (frames != null && animState.currentFrameIndex >= frames.size()) {
                    animState.currentFrameIndex = 0; // Loop animation or stop at last frame
                    // If you want the animation to run only once, you would stop here.
                    // For movement, we will end the animation when reaching the destination.
                }
                animState.lastFrameTime = System.currentTimeMillis();
            }

            // Update interpolation position
            currentStep[0]++;
            if (currentStep[0] >= TOTAL_ANIM_STEPS) {
                animState.isAnimatingMovement = false; // Stop movement flag
                animState.currentAnimationType = "idle"; // Switch to idle after movement
                animState.currentFrameIndex = 0; // Reset frame
                animState.lastFrameTime = System.currentTimeMillis(); // Reset time

                ((Timer) e.getSource()).stop();
                this.pieceToAnimate = null; // Clear animating piece
                repaint();
                if (onAnimationCompleteCallback != null) {
                    onAnimationCompleteCallback.run();
                }
            } else {
                double progress = (double) currentStep[0] / TOTAL_ANIM_STEPS;
                animState.animationCurrentScreenPos = new Point(
                        (int) (animState.animationStartPos.x
                                + (animState.animationEndPos.x - animState.animationStartPos.x) * progress),
                        (int) (animState.animationStartPos.y
                                + (animState.animationEndPos.y - animState.animationStartPos.y) * progress));
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

        if (currentBoard == null)
            return;

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
        Point p1Temple = new Point(2, 0);
        g2d.setColor(new Color(0, 0, 255, 80));
        g2d.fillRect(dynamicOffsetX + p1Temple.y * dynamicCellSize,
                dynamicOffsetY + p1Temple.x * dynamicCellSize,
                dynamicCellSize, dynamicCellSize);

        Point p2Temple = new Point(2, currentBoard.getColumns() - 1);
        g2d.setColor(new Color(255, 0, 0, 80));
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
                    AnimationState state = pieceAnimationStates.get(p);
                    // Chỉ vẽ nếu quân cờ không phải là quân đang di chuyển hoặc đã chết
                    if (state == null || (!state.isAnimatingMovement && !state.isAnimatingDeath)) {
                        drawPiece(g2d, p,
                                dynamicOffsetX + col * dynamicCellSize + dynamicCellSize / 2,
                                dynamicOffsetY + row * dynamicCellSize + dynamicCellSize / 2,
                                p.getPlayerId() == 1 ? pawn1IdleAnimation : pawn2IdleAnimation); // Mặc định là idle
                    }
                }
            }
        }

        // Draw animating piece (movement or death)
        if (pieceToAnimate != null) { // This is for the piece currently moving
            AnimationState state = pieceAnimationStates.get(pieceToAnimate);
            if (state != null && state.isAnimatingMovement) {
                drawPiece(g2d, pieceToAnimate, state.animationCurrentScreenPos.x, state.animationCurrentScreenPos.y,
                        pieceToAnimate.getPlayerId() == 1 ? pawn1MovementAnimation : pawn2MovementAnimation);
            }
            // Thêm logic vẽ animation chết nếu cần
        }
    }

    private void drawPiece(Graphics2D g2d, Piece piece, int centerX, int centerY, Map<String, List<Image>> animMap) {
        if (piece == null)
            return;

        AnimationState state = pieceAnimationStates.getOrDefault(piece, new AnimationState());

        if (state.currentAnimationType == null) {
            state.currentAnimationType = "idle"; // Default to idle if not set
            state.currentFrameIndex = 0; // Reset frame index
            state.lastFrameTime = System.currentTimeMillis(); // Reset time
            pieceAnimationStates.put(piece, state); // Save the state
        }

        // Draw the piece using the current animation state
        List<Image> frames = animMap.get(state.currentAnimationType);
        Image currentFrame = null;
        if (frames != null && !frames.isEmpty()) {
            currentFrame = frames.get(state.currentFrameIndex);

            if (!state.isAnimatingMovement && !state.isAnimatingDeath &&
                    System.currentTimeMillis() - state.lastFrameTime > 100) { // Ví dụ: 100ms/frame cho idle
                state.currentFrameIndex = (state.currentFrameIndex + 1) % frames.size();
                state.lastFrameTime = System.currentTimeMillis();
            }
        }

        if (piece.isMaster()) {
            Color pieceColor = (piece.getPlayerId() == 1) ? new Color(0, 0, 200) : new Color(200, 0, 0);
            Color outlineColor = Color.BLACK;

            g2d.setColor(pieceColor);
            g2d.fillOval(centerX - dynamicPieceSize / 2, centerY - dynamicPieceSize / 2, dynamicPieceSize,
                    dynamicPieceSize);
            g2d.setColor(outlineColor);
            g2d.drawOval(centerX - dynamicPieceSize / 2, centerY - dynamicPieceSize / 2, dynamicPieceSize,
                    dynamicPieceSize);
            g2d.setColor(Color.WHITE);
            // Adjust font size based on piece size for better scaling (optional)
            int fontSize = Math.max(8, dynamicPieceSize / 3);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString("M", centerX - fm.stringWidth("M") / 2, centerY + fm.getAscent() / 2 - fm.getDescent() / 2);
        } else {
            if (currentFrame != null) {
                int imageX = centerX - dynamicPieceSize / 2;
                int imageY = centerY - dynamicPieceSize / 2;

                // Nếu bạn cần lật hình ảnh cho Player 2
                if (piece.getPlayerId() == 2) {
                    Graphics2D g2dCopy = (Graphics2D) g2d.create();
                    g2dCopy.translate(centerX, centerY);
                    g2dCopy.scale(1, -1); // Lật theo chiều dọc
                    g2dCopy.translate(-centerX, -centerY);
                    g2dCopy.drawImage(currentFrame, imageX, imageY, dynamicPieceSize, dynamicPieceSize, this);
                    g2dCopy.dispose();
                } else {
                    g2d.drawImage(currentFrame, imageX, imageY, dynamicPieceSize, dynamicPieceSize, this);
                }
            } else {
                // Fallback: draw a square if image not loaded or not found
                int pawnDrawSize = Math.max(8, dynamicPieceSize - 5);
                g2d.setColor(new Color(piece.getPlayerId() == 1 ? 0 : 200, 0, piece.getPlayerId() == 1 ? 200 : 0));
                g2d.fillRect(centerX - pawnDrawSize / 2, centerY - pawnDrawSize / 2, pawnDrawSize, pawnDrawSize);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(centerX - pawnDrawSize / 2, centerY - pawnDrawSize / 2, pawnDrawSize, pawnDrawSize);
            }
        }
    }
    /* --- --- --- --- --- */
}