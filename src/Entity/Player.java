package Entity;

import java.util.ArrayList;

public class Player {
    //MoveCard that a player own, limit to 2 per player
    private ArrayList<MoveCard> moveCards;
    // Player id : 1 for blue, 2 for red
    private int id;

    /* -------------------- */
    /* --- Constructors --- */
    public Player(int id) {
        this.id = id;
        moveCards = new ArrayList<>();
    }

    // Copy constructor
    public Player(Player player) {
        this.id = player.id;
        this.moveCards = new ArrayList<>(player.moveCards);
    }
    /* --- --- --- --- --- */


    /* -------------- */
    /* --- Getter --- */
    /**
     * @return The ID of the player.
     */
    public int getId() {
        return id;
    }

    
    /**
     * @return A list of move cards that the player owns.
     */
    public ArrayList<MoveCard> getMoveCards() {
        return moveCards;
    }

    /* --- --- --- --- --- */


    /* -------------- */
    /* --- Setter --- */
    /**
     * Sets the player's ID.
     * @param id The new ID for the player.
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Sets the player's move cards.
     * @param moveCards The new list of move cards for the player.
     */
    public void setMoveCards(ArrayList<MoveCard> moveCards) {
        if (moveCards != null) {
            this.moveCards = new ArrayList<>(moveCards);
        } else {
            this.moveCards = new ArrayList<>();
        }
    }

    /* --- --- --- --- --- */


    /* ----------------- */
    /* --- Utilities --- */
    // Add a move card to the player's list of move cards. (< 2 per player)
    public boolean addMoveCard(MoveCard moveCard) {
        if (moveCard == null || moveCards.size() >= 2) {
            return false; // Cannot add null or exceed limit
        }
        moveCards.add(moveCard);
        return true;
    }

    // Remove a move card from the player's list of move cards.
    public boolean removeMoveCard(MoveCard moveCard) {
        if (moveCard == null || !moveCards.contains(moveCard)) {
            return false; // Cannot remove null or non-existent card
        }
        moveCards.remove(moveCard);
        return true;
    }

    public boolean hasMoveCard(MoveCard moveCard) {
        if (moveCard == null) {
            return false; // Cannot check for null move card
        }
        return moveCards.contains(moveCard);
    }

    public boolean exchangeMoveCard(MoveCard oldCard, MoveCard newCard) {
        if (oldCard == null || newCard == null) {
            return false;
        }
        if (this.moveCards.remove(oldCard)) {
            return this.moveCards.add(newCard);
        }
        return false; // cardToRemove was not in hand
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player{id=").append(id).append(", moveCards=[");
        for (int i = 0; i < moveCards.size(); i++) {
            sb.append(moveCards.get(i).getCardName()); // Assuming MoveCard has getName()
            if (i < moveCards.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
    /* --- --- --- --- --- */
}
