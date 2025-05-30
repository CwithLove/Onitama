package Boundary;

import Controller.GameController;
import Entity.MoveCard;
import java.awt.*;
import java.util.ArrayList;
import java.util.List; // Import List
import javax.swing.*;

public class PlayerCardsPanel extends JPanel {
    // Attributes
    private GameController controller;
    // Use List interface for flexibility, initialized with ArrayList
    private ArrayList<MoveCard> cards; 
    private int playerId;
    private MoveCard selectedCard = null;
    // Use List interface for flexibility, initialized with ArrayList
    private ArrayList<CardComponent> cardComponents; 

    /* ------------------- */
    /* --- Constructor --- */
    public PlayerCardsPanel(GameController controller, int playerId) {
        this.controller = controller;
        this.playerId = playerId;
        this.cards = new ArrayList<>();
        this.cardComponents = new ArrayList<>();

        // Use GridLayout (0 rows means any number, 1 column)
        // This will make each card component take equal vertical space.
        // Add gaps between components.
        setLayout(new GridLayout(0, 1, 0, 5)); // 0 rows, 1 col, 0 hgap, 5 vgap
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add some padding around the panel

        // Set a preferred width for the panel. Height will be determined by cards.
        // The preferred height for each card is 100, so for 2 cards it's roughly 2*100 + gaps.
        setPreferredSize(new Dimension(160, 215)); // Approx. 150 width for card + padding, 2*100 height + vgap
    }
    /* --- --- --- --- --- */


    /* --------------- */
    /* --- Setters --- */
    public void setSelectedCard(MoveCard card) {
        this.selectedCard = card;
        // Update the border for the card components
        for (CardComponent cc : cardComponents) {
            // Check if the card belongs to the current player and is the selected one
            if (cc.getCard().equals(selectedCard) && 
                controller.getGameState().getCurrentPlayerId() == this.playerId) {
                cc.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
            } else {
                cc.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        }
        // No need to call repaint() on this panel directly, 
        // as CardComponent itself might repaint if its border changes.
        // However, if CardComponent doesn't repaint on setBorder, then do:
        // repaint(); 
        // It's generally safer to repaint the children if their appearance changes.
        for(CardComponent cc : cardComponents) {
            cc.repaint();
        }
    }
    /* --- --- --- --- --- */


    /* ------------------- */
    /* --- Update View --- */
    // Method signature changed to accept List<MoveCard> for consistency with Player.getMoveCards()
    public void updateCards(List<MoveCard> newCards) { 
        this.cards = new ArrayList<>(newCards); // Create a copy
        
        // Try to preserve selection if the selected card is still in the new list of cards
        MoveCard previouslySelected = this.selectedCard;
        this.selectedCard = null; // Reset first
        if (previouslySelected != null && this.cards.contains(previouslySelected)) {
            // Only re-select if it's still this player's turn and the card is theirs
            if (controller.getGameState().getCurrentPlayerId() == this.playerId && 
                controller.getGameState().getCurrentPlayer().hasMoveCard(previouslySelected)) {
                 this.selectedCard = previouslySelected;
            } else if (controller.getSelectedCard() != null && controller.getSelectedCard().equals(previouslySelected)) {
                 // If the controller still thinks this card is selected globally (e.g. by other player),
                 // but it's not this player's turn to have it highlighted as "active selection"
                 // we might not want to set this.selectedCard here.
                 // For now, if it's in the list, and was selected, keep it for border logic.
                 this.selectedCard = previouslySelected; // This might need refinement based on exact selection logic
            }
        }


        removeAll(); // Remove old card components
        cardComponents.clear();

        for (MoveCard card : cards) {
            // The preferred size (150, 100) is a hint for CardComponent.
            // GridLayout will ultimately determine its actual size.
            CardComponent cardComp = new CardComponent(card, controller, 150, 100, playerId); 
            // cardComp.setAlignmentX(Component.CENTER_ALIGNMENT); // Not needed for GridLayout

            // Highlight if it is the card currently selected by this player
            if (card.equals(selectedCard) && 
                controller.getGameState().getCurrentPlayerId() == this.playerId) {
                cardComp.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
            } else {
                cardComp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }

            add(cardComp);
            cardComponents.add(cardComp);
            // No need for Box.createRigidArea with GridLayout's vgap
        }
        revalidate();
        repaint();
    }
    /* --- --- --- --- --- */
}
