package Entity;

import java.awt.*;
import java.util.List;

public class Player {
    //MoveCard that a player own, limit to 2 per player
    private List<MoveCard> moveCards = new java.util.ArrayList<>();
    // List of Piece that a player own, 1 Master + 4 Pawn
    private List<Piece> pieces = new java.util.ArrayList<>();
    // Player id : 1 for blue, 2 for red
    private int id;

    public Player(int id) {
        this.id = id;

    }
    /**
     * @return The ID of the player.
     */
    public int getId() {
        return id;
    }

    /**
     * Adds a move card to the player's hand if the card is valid and the player has less than 2 cards.
     * @param moveCard The move card to add.
     * @return True if the card was added successfully, false otherwise.
     */
    public boolean addMoveCard(MoveCard moveCard) {
        if (Deck.valid(moveCard) && moveCards.size() < 2) {
            moveCards.add(moveCard);
            return true;
        }
        return false;
    }
    /**
     * Get all the piece belong to the player from the board.
     * @param board The game board where the piece is placed.
     * @return True if the piece was return successfully, false otherwise.
     */
    public boolean setPiece(Board board){
        if (board == null) {
            return false;
        }
        pieces = board.getPiecesByPlayerId(id);
        return pieces != null && !pieces.isEmpty();
    }
    /**
     * play a move card from the player's hand.
     * @param moveCard The move card to play.
     * @param piece The piece to move.
     * @return The move card if the move was successful, null if otherwise.
     */
    public MoveCard playMoveCard(MoveCard moveCard, Piece piece, Board board, Point targetPosition) {
        if (moveCards.contains(moveCard) && pieces.contains(piece)) {
            // TODO: Check if the position is valid according to the MoveCard rules(code in MoveCard.java)
            if (moveCards.validPosition(piece, board, targetPosition)) {
                moveCards.remove(moveCard);
                //TODO: Check if the move is valid according to the game rules(code in Board.java)
                board.movePiece(piece, targetPosition);
                return moveCard;
            }
        }
        return null;
    }
}
