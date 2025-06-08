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
    private Piece pieceToAnimate; // The piece currently undergoing any animation (move, attack)
    private Timer animationTimer; // Main timer for move/attack sequences
    private Runnable onAnimationCompleteCallback; // Callback after the entire sequence (move + attack if any)

    private final int ANIMATION_DELAY = 30; // Delay in milliseconds for UI redraw rate (smoother)
    private final int MOVEMENT_INTERPOLATION_STEPS = 20; // Total steps for piece position interpolation
    private final int MOVEMENT_FRAME_DURATION_MS = 60; // Duration of each movement sprite frame
    private final int ATTACK_ANIMATION_FRAME_DURATION_MS = 80; // Duration of each attack sprite frame
    private final int DEATH_ANIMATION_FRAME_DURATION_MS = 100; // Duration of each death sprite frame

    // Cells Assets
    private Map<String, Image> borders;
    private Map<String, Image> floors;

    // Animation assets (ensure these are loaded correctly in loadAnimationAssets)
    private Map<String, List<Image>> pawn1IdleAnimation;
    private Map<String, List<Image>> pawn1MovementAnimation;
    private Map<String, List<Image>> pawn1AttackAnimation;
    private Map<String, List<Image>> pawn1DeathAnimation;

    private Map<String, List<Image>> pawn2IdleAnimation;
    private Map<String, List<Image>> pawn2MovementAnimation;
    private Map<String, List<Image>> pawn2AttackAnimation;
    private Map<String, List<Image>> pawn2DeathAnimation;

    // Animation state per piece
    private Map<Piece, AnimationState> pieceAnimationStates;

    // Timer for idle animations
    private Timer idleAnimationTimer;
    private final int IDLE_ANIMATION_REFRESH_RATE = 100;

    // List to manage pieces undergoing death animation separately
    private List<Piece> piecesAnimatingDeath = new ArrayList<>();
    private Map<Piece, Timer> deathAnimationTimers = new HashMap<>(); // To manage individual death timers

    // Piece staged for death (to be drawn idle until its death animation starts)
    private Piece pieceStagedForDeath = null;
    private Point pieceStagedForDeathOriginalPos = null; // Logical board coordinates

    private class AnimationState {
        String currentAnimationType; // "idle", "movement", "attack", "death"
        int currentFrameIndex;
        long lastFrameTime;
        boolean isAnimatingSequence; // True if piece is in a move/attack/death sequence managed by animationTimer or
                                     // deathTimer

        // For movement/attack sequence
        Point animationStartScreenPos;
        Point animationEndScreenPos;
        Point animationCurrentScreenPos;
        int interpolationStep;

        public AnimationState() {
            this.currentAnimationType = "idle";
            this.isAnimatingSequence = false;
        }
    }

    /* --- Constructors --- */
    public BoardPanel(GameController controller) {
        this.controller = controller;
        this.possibleMoves = new ArrayList<>();

        pieceAnimationStates = new HashMap<>();
        loadAnimationAssets(); // This will call loadBorders() and loadFloors()

        idleAnimationTimer = new Timer(IDLE_ANIMATION_REFRESH_RATE, e -> {
            if (isVisible() && currentBoard != null)
                repaint();
        });
        idleAnimationTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((animationTimer != null && animationTimer.isRunning()) || !deathAnimationTimers.isEmpty()
                        || pieceStagedForDeath != null) {
                    // Prevent clicks if any major animation (move/attack/death) or staging is
                    // active
                    return;
                }
                if (currentBoard == null)
                    return;

                int boardPixelX = e.getX() - dynamicOffsetX - dynamicCellSize;
                int boardPixelY = e.getY() - dynamicOffsetY - dynamicCellSize;
                int col = boardPixelX / dynamicCellSize;
                int row = boardPixelY / dynamicCellSize;

                if (boardPixelX >= 0 && boardPixelX < currentBoard.getColumns() * dynamicCellSize &&
                        boardPixelY >= 0 && boardPixelY < currentBoard.getRows() * dynamicCellSize) {
                    if (controller.getGameState().getBoard().isValidPosition(row, col)) {
                        controller.handleCellClick(row, col);
                    }
                }
            }
        });
    }

    /* ------------------- */
    /* --- Load Assets --- */
    private void loadAnimationAssets() {
        // Pawn animations
        pawn1IdleAnimation = loadFramesSafely("Pawn/Pawn_1/Idle", "sprite_", 0, 6);
        pawn1MovementAnimation = loadFramesSafely("Pawn/Pawn_1/Movement", "sprite_", 0, 10);
        pawn1AttackAnimation = loadFramesSafely("Pawn/Pawn_1/Attack", "sprite_", 0, 9);
        pawn1DeathAnimation = loadFramesSafely("Pawn/Pawn_1/Death", "sprite_", 0, 17);

        pawn2IdleAnimation = loadFramesSafely("Pawn/Pawn_2/Idle", "sprite_", 0, 6);
        pawn2MovementAnimation = loadFramesSafely("Pawn/Pawn_2/Movement", "sprite_", 0, 10);
        pawn2AttackAnimation = loadFramesSafely("Pawn/Pawn_2/Attack", "sprite_", 0, 15);
        pawn2DeathAnimation = loadFramesSafely("Pawn/Pawn_2/Death", "sprite_", 0, 15);

        // Board tile assets
        borders = loadBorders();
        floors = loadFloors();
    }

    private Map<String, Image> loadBorders() {
        Map<String, Image> bordersMap = new HashMap<>();
        String[] sideBorderNames = { "Down", "Left", "Right", "Up" };

        try {
            for (String borderName : sideBorderNames) {
                Image borderImage = ResourceLoader.loadImage("res/Map/Border/Border_" + borderName + ".png");
                if (borderImage != null) {
                    bordersMap.put(borderName, borderImage);
                } else {
                    System.err.println("Warning: Failed to load border image: res/Map/Border_" + borderName + ".png");
                }
            }
            Image cornerImage = ResourceLoader.loadImage("res/Map/Border/Border_Down_Left.png");
            if (cornerImage != null) {
                bordersMap.put("Down_Left", cornerImage);
            } else {
                System.err.println("Warning: Failed to load corner image: res/Map/Border/Border_Down_Left.png");
            }
            cornerImage = ResourceLoader.loadImage("res/Map/Border/Border_Down_Right.png");
            if (cornerImage != null) {
                bordersMap.put("Down_Right", cornerImage);
            } else {
                System.err.println("Warning: Failed to load corner image: res/Map/Border/Border_Down_Right.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading border images: " + e.getMessage());
        }
        return bordersMap;
    }

    private Map<String, Image> loadFloors() {
        Map<String, Image> floorsMap = new HashMap<>();
        try {
            for (int i = 1; i <= 2; i++) { // Assuming Floor_1.png and Floor_2.png
                Image floorImage = ResourceLoader.loadImage("res/Map/Floor/Floor_" + i + ".png");
                if (floorImage != null) {
                    floorsMap.put("floor_" + i, floorImage);
                } else {
                    System.err.println("Warning: Failed to load floor image: res/Map/Floor/Floor_" + i + ".png");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading floor images: " + e.getMessage());
        }
        return floorsMap;
    }

    private Map<String, List<Image>> loadFramesSafely(String path, String prefix, int startIdx, int endIdx) {
        Map<String, List<Image>> animations = new HashMap<>();
        List<Image> frames = new ArrayList<>();
        String animationName = path.substring(path.lastIndexOf("/") + 1).toLowerCase();

        for (int i = startIdx; i < endIdx; i++) {
            String fileName = prefix + String.format("%02d", i) + ".png";
            try {
                Image img = ResourceLoader.loadImage("res/" + path + "/" + fileName);
                if (img != null) {
                    frames.add(img);
                } else {
                    System.err.println("Warning: Loaded null image for /res/" + path + "/" + fileName);
                }
            } catch (Exception e) {
                System.err.println("Error loading image: /res/" + path + "/" + fileName + " - " + e.getMessage());
            }
        }
        if (frames.isEmpty()) {
            System.err.println("Warning: No frames loaded for animation: " + path);
        }
        animations.put(animationName, frames);
        return animations;
    }

    /* ------------------- */
    /* --- Update View --- */
    private void updateDynamicDimensions() {
        if (currentBoard == null || getWidth() == 0 || getHeight() == 0) {
            this.dynamicCellSize = 60;
            this.dynamicPieceSize = this.dynamicCellSize * 2 / 3;
            this.dynamicOffsetX = 0;
            this.dynamicOffsetY = 0;
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int numCols = currentBoard.getColumns() + 2; // Including borders
        int numRows = currentBoard.getRows() + 2; // Including borders
        int cellSizeBasedOnWidth = panelWidth / numCols;
        int cellSizeBasedOnHeight = panelHeight / numRows;
        this.dynamicCellSize = Math.min(cellSizeBasedOnWidth, cellSizeBasedOnHeight);
        this.dynamicPieceSize = Math.max(10, this.dynamicCellSize * 2 / 3);
        int boardPixelWidth = numCols * this.dynamicCellSize;
        int boardPixelHeight = numRows * this.dynamicCellSize;
        this.dynamicOffsetX = (panelWidth - boardPixelWidth) / 2;
        this.dynamicOffsetY = (panelHeight - boardPixelHeight) / 2;
    }

    @Override
    public Dimension getPreferredSize() {
        int defaultCellSize = 60;
        if (currentBoard != null) {
            return new Dimension(currentBoard.getColumns() * defaultCellSize + 50,
                    currentBoard.getRows() * defaultCellSize + 50); // Add some padding for borders
        }
        return new Dimension(5 * defaultCellSize + 50, 5 * defaultCellSize + 50);
    }

    public void updateBoard(Board board) {
        this.currentBoard = board;
        revalidate();
        repaint();
    }

    /* --------------- */
    /* --- Setters --- */
    public void setSelectedPiece(Piece piece) {
        this.selectedPiece = piece;
    }

    public void setPossibleMoves(List<Point> moves) {
        this.possibleMoves = (moves != null) ? moves : new ArrayList<>();
    }

    // Methods for staging death animation
    public void setPieceStagedForDeath(Piece piece, Point originalBoardPos) {
        this.pieceStagedForDeath = piece;
        this.pieceStagedForDeathOriginalPos = originalBoardPos;
        repaint();
    }

    public void clearPieceStagedForDeath() {
        this.pieceStagedForDeath = null;
        this.pieceStagedForDeathOriginalPos = null;
        repaint();
    }

    /* ----------------- */
    /* --- Animation --- */

    public void animateMove(Piece piece, Point boardFromPos, Point boardToPos, boolean isCapture,
            Runnable onAnimationComplete) {
        updateDynamicDimensions();

        this.pieceToAnimate = piece;
        this.onAnimationCompleteCallback = onAnimationComplete;

        AnimationState state = pieceAnimationStates.computeIfAbsent(piece, k -> new AnimationState());
        state.isAnimatingSequence = true;
        state.currentAnimationType = "movement";
        state.currentFrameIndex = 0;
        state.lastFrameTime = System.currentTimeMillis();
        state.interpolationStep = 0;

        state.animationStartScreenPos = new Point(
                dynamicOffsetX + (boardFromPos.y) * dynamicCellSize + dynamicCellSize / 2,
                dynamicOffsetY + (boardFromPos.x) * dynamicCellSize + dynamicCellSize / 2);
        state.animationEndScreenPos = new Point(
                dynamicOffsetX + (boardToPos.y) * dynamicCellSize + dynamicCellSize / 2,
                dynamicOffsetY + (boardToPos.x) * dynamicCellSize + dynamicCellSize / 2);
        state.animationCurrentScreenPos = new Point(state.animationStartScreenPos);

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(ANIMATION_DELAY, e -> {
            Piece currentAnimatingPiece = this.pieceToAnimate;
            if (currentAnimatingPiece == null) {
                ((Timer) e.getSource()).stop();
                return;
            }
            AnimationState animState = pieceAnimationStates.get(currentAnimatingPiece);

            if (animState == null || !animState.isAnimatingSequence) {
                ((Timer) e.getSource()).stop();
                cleanupAfterAnimation(currentAnimatingPiece);
                if (this.onAnimationCompleteCallback != null) {
                    this.onAnimationCompleteCallback.run();
                }
                return;
            }

            if (animState.currentAnimationType.equals("movement")) {
                if (System.currentTimeMillis() - animState.lastFrameTime > MOVEMENT_FRAME_DURATION_MS) {
                    animState.currentFrameIndex++;
                    Map<String, List<Image>> animSet = (currentAnimatingPiece.getPlayerId() == 1)
                            ? pawn1MovementAnimation
                            : pawn2MovementAnimation;
                    List<Image> movementFrames = animSet.get("movement");
                    if (movementFrames != null && !movementFrames.isEmpty()
                            && animState.currentFrameIndex >= movementFrames.size()) {
                        animState.currentFrameIndex = 0;
                    }
                    animState.lastFrameTime = System.currentTimeMillis();
                }

                animState.interpolationStep++;
                if (animState.interpolationStep >= MOVEMENT_INTERPOLATION_STEPS) {
                    animState.animationCurrentScreenPos.setLocation(animState.animationEndScreenPos);

                    if (isCapture) {
                        animState.currentAnimationType = "attack";
                        animState.currentFrameIndex = 0;
                        animState.lastFrameTime = System.currentTimeMillis();
                    } else {
                        animState.isAnimatingSequence = false;
                        animState.currentAnimationType = "idle";
                        ((Timer) e.getSource()).stop();
                        cleanupAfterAnimation(currentAnimatingPiece);
                        if (this.onAnimationCompleteCallback != null) {
                            this.onAnimationCompleteCallback.run();
                        }
                    }
                } else {
                    double progress = (double) animState.interpolationStep / MOVEMENT_INTERPOLATION_STEPS;
                    animState.animationCurrentScreenPos.x = (int) (animState.animationStartScreenPos.x
                            + (animState.animationEndScreenPos.x - animState.animationStartScreenPos.x) * progress);
                    animState.animationCurrentScreenPos.y = (int) (animState.animationStartScreenPos.y
                            + (animState.animationEndScreenPos.y - animState.animationStartScreenPos.y) * progress);
                }
            } else if (animState.currentAnimationType.equals("attack")) {
                if (System.currentTimeMillis() - animState.lastFrameTime > ATTACK_ANIMATION_FRAME_DURATION_MS) {
                    animState.currentFrameIndex++;
                    animState.lastFrameTime = System.currentTimeMillis();
                }

                Map<String, List<Image>> animSet = (currentAnimatingPiece.getPlayerId() == 1) ? pawn1AttackAnimation
                        : pawn2AttackAnimation;
                List<Image> attackFrames = animSet.get("attack");

                if (attackFrames == null || attackFrames.isEmpty()
                        || animState.currentFrameIndex >= attackFrames.size()) {
                    animState.isAnimatingSequence = false;
                    animState.currentAnimationType = "idle";
                    ((Timer) e.getSource()).stop();
                    cleanupAfterAnimation(currentAnimatingPiece);
                    if (this.onAnimationCompleteCallback != null) {
                        this.onAnimationCompleteCallback.run();
                    }
                }
            }
            repaint();
        });
        animationTimer.start();
        repaint();
    }

    private void cleanupAfterAnimation(Piece piece) {
        if (piece != null) {
            AnimationState state = pieceAnimationStates.get(piece);
            if (state != null) {
                state.isAnimatingSequence = false;
                state.currentAnimationType = "idle";
                state.currentFrameIndex = 0;
            }
        }
        if (this.pieceToAnimate == piece) {
            this.pieceToAnimate = null;
        }
    }

    public void animateDeath(Piece piece, Runnable onDeathComplete) {
        if (piece == null) {
            if (onDeathComplete != null)
                onDeathComplete.run();
            return;
        }
        updateDynamicDimensions();

        AnimationState state = pieceAnimationStates.computeIfAbsent(piece, k -> new AnimationState());
        state.currentAnimationType = "death";
        state.isAnimatingSequence = true;
        state.currentFrameIndex = 0;
        state.lastFrameTime = System.currentTimeMillis();
        state.animationCurrentScreenPos = new Point(
                dynamicOffsetX + piece.getPosY() * dynamicCellSize + dynamicCellSize / 2,
                dynamicOffsetY + piece.getPosX() * dynamicCellSize + dynamicCellSize / 2);

        if (!piecesAnimatingDeath.contains(piece)) {
            piecesAnimatingDeath.add(piece);
        }

        Map<String, List<Image>> deathAnimSet = (piece.getPlayerId() == 1) ? pawn1DeathAnimation : pawn2DeathAnimation;
        List<Image> deathFrames = deathAnimSet.get("death");

        if (deathFrames == null || deathFrames.isEmpty()) {
            System.err.println("No death animation frames for piece: " + piece);
            piecesAnimatingDeath.remove(piece);
            cleanupAfterAnimation(piece);
            if (onDeathComplete != null)
                onDeathComplete.run();
            repaint();
            return;
        }

        Timer existingTimer = deathAnimationTimers.remove(piece);
        if (existingTimer != null) {
            existingTimer.stop();
        }

        Timer pieceDeathTimer = new Timer(DEATH_ANIMATION_FRAME_DURATION_MS, null);
        pieceDeathTimer.addActionListener(e -> {
            AnimationState currentPieceState = pieceAnimationStates.get(piece);
            if (currentPieceState == null || !currentPieceState.isAnimatingSequence
                    || !currentPieceState.currentAnimationType.equals("death")) {
                ((Timer) e.getSource()).stop();
                deathAnimationTimers.remove(piece);
                piecesAnimatingDeath.remove(piece);
                cleanupAfterAnimation(piece);
                if (onDeathComplete != null) {
                    onDeathComplete.run();
                }
                repaint();
                return;
            }

            currentPieceState.currentFrameIndex++;
            if (currentPieceState.currentFrameIndex >= deathFrames.size()) {
                ((Timer) e.getSource()).stop();
                deathAnimationTimers.remove(piece);
                piecesAnimatingDeath.remove(piece);
                currentPieceState.isAnimatingSequence = false;
                currentPieceState.currentAnimationType = "idle";

                if (onDeathComplete != null) {
                    onDeathComplete.run();
                }
            }
            repaint();
        });
        deathAnimationTimers.put(piece, pieceDeathTimer);
        pieceDeathTimer.setRepeats(true);
        pieceDeathTimer.start();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateDynamicDimensions();
        if (currentBoard == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // --- Draw Board Cells and Grid Lines ---
        for (int row = 0; row < currentBoard.getRows() + 2; row++) {
            for (int col = 0; col < currentBoard.getColumns() + 2; col++) {
                int cellX = dynamicOffsetX + col * dynamicCellSize;
                int cellY = dynamicOffsetY + row * dynamicCellSize;
                Image floorImageToDraw = null;
                Image borderImageToDraw = null;


                if (col == 0) { // Border LEFT
                    if (row == currentBoard.getRows() + 1) {
                        borderImageToDraw = borders.get("Down_Left");
                    } else {
                        borderImageToDraw = borders.get("Left");
                    }
                } else if (col == currentBoard.getColumns() + 1) { // Border RIGHT
                    if (row == currentBoard.getRows() + 1) {
                        borderImageToDraw = borders.get("Down_Right");
                    } else {
                        borderImageToDraw = borders.get("Right");
                    }
                } else if (row == 0) {
                    borderImageToDraw = borders.get("Up");
                } else if (row == currentBoard.getRows() + 1) {
                    borderImageToDraw = borders.get("Down");
                } 


                if (borderImageToDraw != null) {
                    g2d.drawImage(borderImageToDraw, cellX, cellY, dynamicCellSize, dynamicCellSize, this);
                    continue; // Skip drawing floor if border is present
                }

                if (row != 0 && row != currentBoard.getRows() + 1 && col != 0 && col != currentBoard.getColumns() + 1) {

                    if ((row + col) % 2 == 0) {
                        floorImageToDraw = (floors != null) ? floors.get("floor_1") : null;
                    } else {
                        floorImageToDraw = (floors != null) ? floors.get("floor_2") : null;
                    }

                    if (floorImageToDraw != null) {
                        g2d.drawImage(floorImageToDraw, cellX, cellY, dynamicCellSize, dynamicCellSize, this);
                    } else {
                        // Fallback to drawing colored rectangles if images are not loaded
                        g2d.setColor(((row + col) % 2 == 0) ? new Color(209, 192, 147) : new Color(165, 131, 82));
                        g2d.fillRect(cellX, cellY, dynamicCellSize, dynamicCellSize);
                    }
                }
                // Draw grid lines for each cell
                g2d.setColor(new Color(0, 0, 0, 50)); // Semi-transparent black
                g2d.drawRect(cellX, cellY, dynamicCellSize, dynamicCellSize);
            }
        }

        // Draw Temple Arches (on top of floors/grid, but below pieces/highlights)
        Point p1Temple = new Point(2, 0);
        g2d.setColor(new Color(0, 0, 255, 100)); // Slightly more opaque
        g2d.fillRect(dynamicOffsetX + (p1Temple.y + 1) * dynamicCellSize,
                dynamicOffsetY + (p1Temple.x + 1) * dynamicCellSize,
                dynamicCellSize, dynamicCellSize);

        Point p2Temple = new Point(2, currentBoard.getColumns() - 1);
        g2d.setColor(new Color(255, 0, 0, 100)); // Slightly more opaque
        g2d.fillRect(dynamicOffsetX + (p2Temple.y + 1) * dynamicCellSize,
                dynamicOffsetY + (p2Temple.x + 1) * dynamicCellSize,
                dynamicCellSize, dynamicCellSize);

        // Highlight selected piece's cell
        if (selectedPiece != null && (pieceToAnimate == null || !selectedPiece.equals(pieceToAnimate))) {
            g2d.setColor(new Color(0, 255, 0, 100));
            g2d.fillRect(dynamicOffsetX + (selectedPiece.getPosY() + 1) * dynamicCellSize,
                    dynamicOffsetY + (selectedPiece.getPosX() + 1) * dynamicCellSize,
                    dynamicCellSize, dynamicCellSize);
        }

        // Highlight possible moves
        g2d.setColor(new Color(255, 255, 0, 100));
        for (Point move : possibleMoves) {
            g2d.fillRect(dynamicOffsetX + (move.y + 1) * dynamicCellSize,
                    dynamicOffsetY + (move.x + 1) * dynamicCellSize,
                    dynamicCellSize, dynamicCellSize);
        }

        // Draw pieces from the board (static pieces or those in idle animation)
        for (int row = 0; row < currentBoard.getRows(); row++) {
            for (int col = 0; col < currentBoard.getColumns(); col++) {
                Piece p = currentBoard.getPieceAt(row, col);
                if (p != null) {
                    if (p.equals(this.pieceToAnimate))
                        continue;
                    if (piecesAnimatingDeath.contains(p))
                        continue;
                    if (p.equals(this.pieceStagedForDeath))
                        continue;

                    AnimationState state = pieceAnimationStates.computeIfAbsent(p, k -> new AnimationState());
                    if (!state.isAnimatingSequence) {
                        int centerX = dynamicOffsetX + (col) * dynamicCellSize + dynamicCellSize / 2;
                        int centerY = dynamicOffsetY + (row) * dynamicCellSize + dynamicCellSize / 2;
                        Map<String, List<Image>> animMap = (p.getPlayerId() == 1) ? pawn1IdleAnimation
                                : pawn2IdleAnimation;
                        drawPiece(g2d, p, centerX, centerY, animMap);
                    }
                }
            }
        }

        // Draw the piece that is staged for death (visible but not yet in death
        // animation)
        if (pieceStagedForDeath != null && pieceStagedForDeathOriginalPos != null) {
            if (!piecesAnimatingDeath.contains(pieceStagedForDeath)) {
                AnimationState stagedState = pieceAnimationStates.computeIfAbsent(pieceStagedForDeath,
                        k -> new AnimationState());

                String originalAnimationType = stagedState.currentAnimationType;
                boolean originalIsAnimatingSequence = stagedState.isAnimatingSequence;

                stagedState.currentAnimationType = "idle";
                stagedState.isAnimatingSequence = false;

                int centerX = dynamicOffsetX + (pieceStagedForDeathOriginalPos.y) * dynamicCellSize
                        + dynamicCellSize / 2;
                int centerY = dynamicOffsetY + (pieceStagedForDeathOriginalPos.x) * dynamicCellSize
                        + dynamicCellSize / 2;

                Map<String, List<Image>> animMap = (pieceStagedForDeath.getPlayerId() == 1) ? pawn1IdleAnimation
                        : pawn2IdleAnimation;
                drawPiece(g2d, pieceStagedForDeath, centerX, centerY, animMap);

                stagedState.currentAnimationType = originalAnimationType;
                stagedState.isAnimatingSequence = originalIsAnimatingSequence;
            }
        }

        // Draw piece undergoing move/attack animation
        if (this.pieceToAnimate != null) {
            AnimationState state = pieceAnimationStates.get(this.pieceToAnimate);
            if (state != null && state.isAnimatingSequence && state.animationCurrentScreenPos != null &&
                    (state.currentAnimationType.equals("movement") || state.currentAnimationType.equals("attack"))) {
                Map<String, List<Image>> currentAnimMap = null;
                if (state.currentAnimationType.equals("movement")) {
                    currentAnimMap = (this.pieceToAnimate.getPlayerId() == 1) ? pawn1MovementAnimation
                            : pawn2MovementAnimation;
                } else { // "attack"
                    currentAnimMap = (this.pieceToAnimate.getPlayerId() == 1) ? pawn1AttackAnimation
                            : pawn2AttackAnimation;
                }

                if (currentAnimMap != null && currentAnimMap.get(state.currentAnimationType) != null
                        && !currentAnimMap.get(state.currentAnimationType).isEmpty()) {
                    drawPiece(g2d, this.pieceToAnimate, state.animationCurrentScreenPos.x,
                            state.animationCurrentScreenPos.y, currentAnimMap);
                } else {
                    drawPiece(g2d, this.pieceToAnimate, state.animationCurrentScreenPos.x + 1,
                            state.animationCurrentScreenPos.y + 1,
                            (this.pieceToAnimate.getPlayerId() == 1) ? pawn1IdleAnimation : pawn2IdleAnimation);
                }
            }
        }

        // Draw pieces undergoing death animation
        for (Piece p : new ArrayList<>(piecesAnimatingDeath)) {
            AnimationState state = pieceAnimationStates.get(p);
            if (p != null && state != null && state.isAnimatingSequence && state.currentAnimationType.equals("death")
                    && state.animationCurrentScreenPos != null) {
                Map<String, List<Image>> animMap = (p.getPlayerId() == 1) ? pawn1DeathAnimation : pawn2DeathAnimation;
                if (animMap.get("death") != null && !animMap.get("death").isEmpty()) {
                    drawPiece(g2d, p, state.animationCurrentScreenPos.x, state.animationCurrentScreenPos.y, animMap);
                }
            }
        }
    }

    private void drawPiece(Graphics2D g2d, Piece piece, int centerX, int centerY, Map<String, List<Image>> animMap) {
        if (piece == null)
            return;

        AnimationState state = pieceAnimationStates.computeIfAbsent(piece, k -> new AnimationState());

        List<Image> frames = null;
        String animationTypeToDraw = state.currentAnimationType;

        if (animMap != null) {
            frames = animMap.get(animationTypeToDraw);
            if (frames == null || frames.isEmpty()) {
                frames = animMap.get("idle");
                if (frames == null || frames.isEmpty()) {
                    Map<String, List<Image>> idleMap = (piece.getPlayerId() == 1) ? pawn1IdleAnimation
                            : pawn2IdleAnimation;
                    if (idleMap != null)
                        frames = idleMap.get("idle");
                }
            }
        }

        Image currentFrameImage = null;
        if (frames != null && !frames.isEmpty()) {
            if (state.currentFrameIndex >= frames.size() || state.currentFrameIndex < 0)
                state.currentFrameIndex = 0;
            currentFrameImage = frames.get(state.currentFrameIndex);

            if (!state.isAnimatingSequence && state.currentAnimationType.equals("idle")) {
                if (System.currentTimeMillis() - state.lastFrameTime > IDLE_ANIMATION_REFRESH_RATE) {
                    state.currentFrameIndex = (state.currentFrameIndex + 1) % frames.size();
                    state.lastFrameTime = System.currentTimeMillis();
                }
            }
        }

        if (piece.isMaster()) {
            Color pieceColor = (piece.getPlayerId() == 1) ? new Color(0, 0, 200) : new Color(200, 0, 0);
            g2d.setColor(pieceColor);
            g2d.fillOval(centerX + dynamicCellSize - dynamicPieceSize / 2,
                    centerY + dynamicCellSize - dynamicPieceSize / 2, dynamicPieceSize,
                    dynamicPieceSize);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(centerX + dynamicCellSize - dynamicPieceSize / 2,
                    centerY + dynamicCellSize - dynamicPieceSize / 2, dynamicPieceSize,
                    dynamicPieceSize);
            g2d.setColor(Color.WHITE);
            int fontSize = Math.max(8, dynamicPieceSize / 3);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString("M", centerX + dynamicCellSize - fm.stringWidth("M") / 2,
                    centerY + dynamicCellSize + fm.getAscent() / 2 - fm.getDescent() / 2);
        } else {
            if (currentFrameImage != null) {
                int imageX = centerX + dynamicCellSize - dynamicPieceSize / 2;
                int imageY = centerY + dynamicCellSize - dynamicPieceSize / 2;
                if (piece.getPlayerId() == 2) {
                    Graphics2D g2dCopy = (Graphics2D) g2d.create();
                    g2dCopy.translate(imageX + dynamicPieceSize, imageY);
                    g2dCopy.scale(-1, 1);
                    g2dCopy.drawImage(currentFrameImage, 0, 0, dynamicPieceSize, dynamicPieceSize, this);
                    g2dCopy.dispose();
                } else {
                    g2d.drawImage(currentFrameImage, imageX, imageY, dynamicPieceSize, dynamicPieceSize, this);
                }
            } else {
                int pawnDrawSize = Math.max(8, dynamicPieceSize - 5);
                g2d.setColor((piece.getPlayerId() == 1) ? new Color(50, 50, 255) : new Color(255, 50, 50));
                g2d.fillRect(centerX - pawnDrawSize / 2, centerY - pawnDrawSize / 2, pawnDrawSize, pawnDrawSize);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(centerX - pawnDrawSize / 2, centerY - pawnDrawSize / 2, pawnDrawSize, pawnDrawSize);
            }
        }
    }
}