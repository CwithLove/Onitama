package Entity;

import java.awt.Point;

public class Pawn extends Piece {

    /* ------------------- */
    /* --- Contructors --- */

    // Default constructor
    public Pawn(int playerId, int x, int y) {
        super(playerId, x, y, TypePiece.PAWN); // Call superclass constructor and set type
    }

    // Constructor with Point parameter
    public Pawn(int playerId, Point position) {
        super(playerId, position, TypePiece.PAWN); // Call superclass constructor and set type
    }

    // Copy constructor
    public Pawn(Pawn pawn) {
        super(pawn); // Call the Piece copy constructor
        // Ensure type is PAWN if superclass copy doesn't guarantee it
        if (this.getType() != TypePiece.PAWN) {
            super.setType(TypePiece.PAWN);
        }
    }
    /* --- --- --- --- --- */

}