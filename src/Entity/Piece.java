package Entity;

import java.awt.Point;

public abstract class Piece {
    private int playerId;
    private Point position;
    private TypePiece type; // Type is now set by subclass constructor via super()

    /* ------------------- */
    /* --- Contructors --- */

    // Main constructor for subclasses to call
    public Piece(int playerId, int x, int y, TypePiece type) {
        this.playerId = playerId;
        this.position = new Point(x, y);
        this.type = type; // Initialize type
    }

    // Constructor with Point parameter
    public Piece(int playerId, Point position, TypePiece type) {
        this(playerId, position.x, position.y, type);
    }

    // Copy constructor
    public Piece(Piece otherPiece) {
        this(otherPiece.playerId, new Point(otherPiece.position), otherPiece.type);
    }
    /* --- --- --- --- --- */


    /* -------------- */
    /* --- Getter --- */
    public int getPlayerId() {
        return this.playerId;
    }

    public Point getPosition() {
        // Return a new Point to prevent external modification of the internal Point object
        return new Point(this.position);
    }

    public int getPosX() {
        return this.position.x;
    }

    public int getPosY() {
        return this.position.y;
    }

    public TypePiece getType() {
        return this.type;
    }
    /* --- --- --- --- --- */


    /* -------------- */
    /* --- Setter --- */
    // playerId is usually set at creation and might not need a public setter
    // or could be package-private/protected if needed by game logic within the entity package.
    // For simplicity, keeping it public for now.
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setPosition(Point position) {
        // Use a new Point to avoid sharing external mutable Point objects
        this.position = new Point(position);
    }

    public void setPosition(int x, int y) {
        this.position.setLocation(x, y);
    }

    // Type is generally immutable for a piece (a Pawn is always a Pawn).
    // If type can change, this setter is fine. Otherwise, it should be removed
    // or made protected/package-private. For now, let's assume it might be needed.
    // However, for Pawn and Master, the type is fixed.
    protected void setType(TypePiece type) { // Changed to protected
        this.type = type;
    }
    /* --- --- --- --- --- */

    /* -------------- */
    /* --- Status --- */
    // Assuming Player class has a getId() method
    public boolean isBelongTo(Player player) {
        if (player == null) return false;
        return this.playerId == player.getId();
    }

    public boolean isBelongTo(int playerId) {
        return this.playerId == playerId;
    }

    public boolean isMaster() {
        return this.type == TypePiece.MASTER;
    }

    public boolean isPawn() {
        return this.type == TypePiece.PAWN;
    }
    /* --- --- --- --- --- */

    /* ----------------- */
    /* --- Utilities --- */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return playerId == piece.playerId &&
               position.equals(piece.position) &&
               type == piece.type;
    }

    @Override
    public int hashCode() {
        int result = playerId;
        result = 31 * result + position.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public String toString(boolean debug) {
        if (debug) {
            return "Piece{" +
                   "playerId=" + playerId +
                   ", position=" + position +
                   ", type=" + type +
                   '}';
        } else {
            return (type == TypePiece.MASTER ? "M" : "P") + playerId;
        }
    }

    @Override
    public String toString() {
        return toString(false); // Default to non-debug string representation
    }
    /* --- --- --- --- --- */
}