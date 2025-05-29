package Entity;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MoveCard {
    private String cardName;
    private List<Point> moves;

    public MoveCard(String cardName, List<Point> moves) {
        this.cardName = cardName;
        this.moves = moves;
    }
    // copy constructor
    public MoveCard(MoveCard moveCard) {
        this.cardName = moveCard.cardName;
        this.moves = moveCard.moves;
    }
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
        if (o == null || getClass() != o.getClass()) return false;
        MoveCard moveCard = (MoveCard) o;
        return Objects.deepEquals(moves, moveCard.moves);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(moves);
    }
}
