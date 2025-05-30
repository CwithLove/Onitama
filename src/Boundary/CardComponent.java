package Boundary;

import Controller.GameController;
import Entity.MoveCard;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;

public class CardComponent extends JPanel {
    private MoveCard card;
    private GameController controller;
    int playerId; // Player ID for this card component

    // Dynamically calculated sizes
    private int dynamicGridSize;
    private int dynamicNameFontSize;
    private int dynamicGridOffsetX;
    private int dynamicGridOffsetY;
    private int dynamicNameYBaseline; // Y baseline for drawing card name

    /* ------------------- */
    /* --- Constructor --- */
    public CardComponent(MoveCard card, GameController controller, int preferredWidth, int preferredHeight, int playerId) {
        this.card = card;
        this.controller = controller;
        this.playerId = playerId;
        setPreferredSize(new Dimension(preferredWidth, preferredHeight)); // Set preferred size
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // TODO: mouse Pressed and mouse Released
        if (controller != null && card != null) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (controller.getGameState().getCurrentPlayer().hasMoveCard(CardComponent.this.card)) {
                        controller.handleCardSelection(CardComponent.this.card);
                    } else {
                        System.out.println("CardComponent: Not this player's card or turn.");
                    }
                }
            });
        }
    }
    /* --- --- --- --- --- */

    /* --------------- */
    /* --- Getter --- */
    public MoveCard getCard() {
        return card;
    }
    /* --- --- --- --- --- */

    /**
     * Recalculates internal dimensions based on the component's current width and height.
     */
    private void updateDynamicDimensions() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (panelWidth <= 0 || panelHeight <= 0) { // Not visible or laid out
            this.dynamicNameFontSize = 8;
            this.dynamicNameYBaseline = 10;
            this.dynamicGridSize = 1;
            this.dynamicGridOffsetX = 0;
            this.dynamicGridOffsetY = 15;
            return;
        }

        // 1. Determine Name Font Size and Y Position
        // Font size roughly proportional to card height, with min/max.
        this.dynamicNameFontSize = Math.max(8, Math.min(panelHeight / 7, panelWidth / 9));
        Font cardNameFont = new Font("Serif", Font.BOLD, this.dynamicNameFontSize);
        // It's better to get FontMetrics from the Graphics object in paintComponent,
        // but for layout, we can use it from the component itself.
        FontMetrics fm = this.getFontMetrics(cardNameFont); 
        
        int nameAscent = fm.getAscent();
        int nameTopPadding = Math.max(2, panelHeight / 20); // Small top padding for the name
        this.dynamicNameYBaseline = nameAscent + nameTopPadding;
        int nameAreaHeightWithPadding = fm.getHeight() + nameTopPadding + Math.max(3, panelHeight / 25); // Total height for name area + padding below it

        // 2. Determine Grid Size based on remaining space
        // Padding around the 5x5 grid block
        int gridBlockHorizontalPadding = Math.max(3, panelWidth / 12); 
        int gridBlockVerticalPadding = Math.max(3, (panelHeight - nameAreaHeightWithPadding) / 15);

        int availableWidthForGridBlock = panelWidth - 2 * gridBlockHorizontalPadding;
        int availableHeightForGridBlock = panelHeight - nameAreaHeightWithPadding - 2 * gridBlockVerticalPadding;
        
        if (availableWidthForGridBlock <= 0 || availableHeightForGridBlock <= 0) {
            // Fallback if space is too constrained
            availableWidthForGridBlock = Math.max(5, panelWidth - 2 * Math.max(1, panelWidth/20) ); // Minimal padding
            availableHeightForGridBlock = Math.max(5, panelHeight - nameAreaHeightWithPadding - 2 * Math.max(1, panelHeight/20) );
             if (availableHeightForGridBlock <=0) availableHeightForGridBlock = panelHeight / 2; // Desperate
        }

        int gsByWidth = availableWidthForGridBlock / 5;
        int gsByHeight = availableHeightForGridBlock / 5;
        this.dynamicGridSize = Math.max(1, Math.min(gsByWidth, gsByHeight)); // Grid cell must be at least 1x1

        // 3. Determine Grid Offset (centering the grid block in its allocated space)
        int totalGridActualWidth = 5 * this.dynamicGridSize;
        int totalGridActualHeight = 5 * this.dynamicGridSize;

        this.dynamicGridOffsetX = (panelWidth - totalGridActualWidth) / 2;
        // Grid starts below the name area, centered in the space available for the grid block
        this.dynamicGridOffsetY = nameAreaHeightWithPadding + (availableHeightForGridBlock - totalGridActualHeight) / 2 + gridBlockVerticalPadding;
        
        // Ensure grid Y offset is below the name area
        if (this.dynamicGridOffsetY < nameAreaHeightWithPadding) {
            this.dynamicGridOffsetY = nameAreaHeightWithPadding;
        }
         // Ensure grid is not pushed out of bounds if calculations are off for very small sizes
        if (this.dynamicGridOffsetY + totalGridActualHeight > panelHeight) {
            this.dynamicGridOffsetY = Math.max(nameAreaHeightWithPadding, panelHeight - totalGridActualHeight -1);
        }
        if (this.dynamicGridOffsetX + totalGridActualWidth > panelWidth) {
             this.dynamicGridOffsetX = Math.max(0, panelWidth - totalGridActualWidth -1);
        }
    }


    /* -------------- */
    /* --- Render --- */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        updateDynamicDimensions(); // Recalculate sizes based on current component dimensions

        if (card == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Card Background
        g2d.setColor(new Color(240, 220, 180)); // Light parchment color
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(0,0, getWidth()-1, getHeight()-1);


        // Card Name
        Font nameFont = new Font("Serif", Font.BOLD, dynamicNameFontSize);
        g2d.setFont(nameFont);
        FontMetrics fmName = g2d.getFontMetrics(); // Get metrics for the current font
        int nameWidth = fmName.stringWidth(card.getCardName());
        g2d.setColor(Color.BLACK); // Set color for text
        g2d.drawString(card.getCardName(), (getWidth() - nameWidth) / 2, dynamicNameYBaseline);

        // Draw 5x5 grid for moves
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                g2d.setColor(new Color(220, 200, 160)); // Slightly darker grid lines
                g2d.drawRect(dynamicGridOffsetX + c * dynamicGridSize, 
                             dynamicGridOffsetY + r * dynamicGridSize, 
                             dynamicGridSize, dynamicGridSize);
            }
        }

        // Center square (piece's current position)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(dynamicGridOffsetX + 2 * dynamicGridSize, 
                     dynamicGridOffsetY + 2 * dynamicGridSize, 
                     dynamicGridSize, dynamicGridSize);

        // Draw moves (relative to the center [2,2] in the 5x5 grid)
        g2d.setColor(Color.BLUE);
        List<Point> moves = card.getMoves(); // These are (deltaRow, deltaCol) from Entity.Point
        if (this.playerId == 1 || this.playerId == 3) { // Player 1's perspective

            for (Point move : moves) {
                // The card display is consistent: deltaRow points "up" on card (decrease in row index), 
                // deltaCol points "right" on card (increase in col index).
                // Center of grid is (row 2, col 2).
                // A move like (dx, dy) from your Point (which is deltaRow, deltaCol) means:
                // targetRowOnGrid = 2 + move.x (since -ve move.x means 'up' or smaller row index on grid)
                // targetColOnGrid = 2 + move.y
                int targetRowOnGrid = 2 + move.x; 
                int targetColOnGrid = 2 + move.y; 
                
                if (targetRowOnGrid >= 0 && targetRowOnGrid < 5 && targetColOnGrid >=0 && targetColOnGrid < 5) {
                     g2d.fillRect(dynamicGridOffsetX + targetColOnGrid * dynamicGridSize, 
                                  dynamicGridOffsetY + targetRowOnGrid * dynamicGridSize, 
                                  dynamicGridSize, dynamicGridSize);
                }
            } 
        } else {
            for (Point move : moves) {
                int targetRowOnGrid = 2 - move.x; 
                int targetColOnGrid = 2 - move.y; 
                
                if (targetRowOnGrid >= 0 && targetRowOnGrid < 5 && targetColOnGrid >=0 && targetColOnGrid < 5) {
                     g2d.fillRect(dynamicGridOffsetX + targetColOnGrid * dynamicGridSize, 
                                  dynamicGridOffsetY + targetRowOnGrid * dynamicGridSize, 
                                  dynamicGridSize, dynamicGridSize);
                }
            }

        }
        // Draw starting player indicator (small dot or symbol)
        g2d.setColor(card.getStarting() == 1 ? new Color(0,0,200) : new Color(200,0,0)); // Brighter Blue/Red
        int indicatorSize = Math.max(5, dynamicGridSize * 2 / 3);
        int indicatorPadding = Math.max(3, getWidth() / 25);
        g2d.fillOval(getWidth() - indicatorSize - indicatorPadding, indicatorPadding, indicatorSize, indicatorSize); // Top-right corner
    }
    /* --- --- --- --- --- */
}