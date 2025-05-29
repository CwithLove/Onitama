package Entity;

import java.awt.Point;

public interface Piece {
    // Method to get playerId
    int getPlayerId();

    // Method to check if this piece is belong to a Player
    boolean isBelongTo(Player player);

    // Method to get the position of the piece on the board
    Point getPosition();

    // Method to set the position of the piece on the board
    void setPosition(Point position);

    // Method to get the type of the piece (e.g., King, Queen, etc.)
    String getType();

    // Method to check if the piece can move to a given position
    boolean canMoveTo(Point targetPosition);

    // Method to move the piece to a new position
    void moveTo(Point targetPosition);
}
