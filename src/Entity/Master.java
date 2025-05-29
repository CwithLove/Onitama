package Entity;

import java.awt.Point;

public class Master extends Piece {

    /* ------------------- */
    /* --- Contructors --- */
    public Master(int playerId, int x, int y) {
        super(playerId, x, y, TypePiece.MASTER);
    }

    public Master(int playerId, Point position) {
        super(playerId, position, TypePiece.MASTER);
    }

    public Master(Master master) {
        super(master);
        // Ensure type is MASTER if superclass copy doesn't guarantee it
    }
    /* --- --- --- --- --- */

    // Inherits all necessary methods from Piece.
    // isMaster() will return true, isPawn() will return false.
}