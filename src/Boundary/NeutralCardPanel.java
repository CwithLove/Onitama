package Boundary;

import Entity.MoveCard;
import java.awt.*;
import javax.swing.*;

public class NeutralCardPanel extends JPanel {
    // Attributes
    private CardComponent neutralCardComponent;

    /* --- Constructor --- */
    public NeutralCardPanel() {
        // Use BorderLayout to allow the CardComponent to fill this panel
        setLayout(new BorderLayout()); 
        // Set a default preferred size for this panel.
        // The GameView might also set or override this.
        // This preferred size will be for the NeutralCardPanel itself.
        // The CardComponent inside will expand to fill NeutralCardPanel.
        setPreferredSize(new Dimension(150, 100)); // Default preferred size for the panel hosting the card
    }
    /* --- --- --- --- --- */


    /* ------------------- */
    /* --- Update View --- */
    public void updateCard(MoveCard card) {
        removeAll(); // Remove the old card component if it exists
        if (card != null) {
            // Create the CardComponent. It will use its own dynamic drawing.
            // The preferred size (150, 100) passed here is for the CardComponent's
            // own preference, but BorderLayout will resize it.
            neutralCardComponent = new CardComponent(card, null, 150, 100, 3); 
            
            // Highlight the border of the CardComponent based on the starting player
            if (card.getStarting() == 1) { // Player 1's turn color for card
                neutralCardComponent.setBorder(BorderFactory.createLineBorder(new Color(0,0,200), 2)); // Brighter Blue
            } else { // Player 2's turn color for card
                neutralCardComponent.setBorder(BorderFactory.createLineBorder(new Color(200,0,0), 2)); // Brighter Red
            }
            // Add the CardComponent to the center of this panel, so it expands
            add(neutralCardComponent, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }
    /* --- --- --- --- --- */
}
