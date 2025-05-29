package Entity;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MoveCard {
    private final String cardName;
    private final List<Point> moves;
    private final int starting;

    /* --- Constructors --- */
    public MoveCard(String cardName, List<Point> moves, int starting) {
        this.cardName = cardName;
        this.moves = moves;
        this.starting = starting;
    }

    // copy constructor
    public MoveCard(MoveCard moveCard) {
        this.cardName = moveCard.cardName;
        this.moves = new ArrayList<>();
        for (Point move : moveCard.moves) {
            if (move == null) {
                throw new IllegalArgumentException("MoveCard: Move cannot be null");
            }
            this.moves.add(new Point(move.x, move.y)); // Create a new Point to avoid reference issues
        }
        this.starting = moveCard.starting;
    }
    /* --- --- --- --- --- */

    /* --------------- */
    /* --- Getters --- */
    /**
     * @return The name of the move card.
     */
    public String getCardName() {
        return cardName;
    }
    /**
     * @return The moves associated with the move card.
     */
    public List<Point> getMoves() {
        return moves;
    }

    /**
     * @return The starting player of the move card.
     */
    public int getStarting() {
        return starting;
    }
    /* --- --- --- --- --- */


    /* ----------------- */
    /* --- Utilities --- */
    /**
     * Validates if the move card can be played from a given piece's position to a target position on the board.
     * @param piece The piece to move.
     * @param board The game board.
     * @param targetPosition The target position to move to.
     * @return True if the move is valid, false otherwise.
     */
    public boolean validPosition(Piece piece, Board board, Point targetPosition) {
        if (piece == null || board == null || targetPosition == null) {
            return false;
        }
        Point piecePosition = piece.getPosition();
        for (Point move : moves) {
            Point newPosition = new Point(piecePosition.x + move.x, piecePosition.y + move.y);
            if (newPosition.equals(targetPosition)) {
                // Check if the target position is within the bounds of the board
                return board.isValidPosition(targetPosition);
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        MoveCard moveCard = (MoveCard) o;
        return Objects.equals(cardName, moveCard.cardName) && // Card
                Objects.equals(moves, moveCard.moves); // all the list
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardName, moves);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(cardName)
          .append(" => Moves: ");

        for (int i = 0; i < moves.size(); i++) {
            Point move = moves.get(i);
            sb.append("(").append(move.x).append(", ").append(move.y).append(")");
            if (i < moves.size() - 1) {
                sb.append("; ");
            }
        }
        sb.append(", starting=").append(starting);
        return sb.toString();
    }
    /* --- --- --- --- --- */
}
