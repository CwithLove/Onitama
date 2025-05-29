package Entity;

import java.awt.Point;

public class Board {
    private int columns;
    private int rows;
    private Piece[][] grid;

    /* ------------------ */
    /* --- Construtor --- */
    // Default constructor initializes a standard 5x5 board
    public Board(int row, int col) {
        this.columns = col;
        this.rows = row;
        // Initialize the grid with the specified number of rows and columns
        if (row <= 0 || col <= 0) {
            throw new IllegalArgumentException("Row and column counts must be positive.");
        }
        if (row > 5 || col > 5) {
            throw new IllegalArgumentException("Row and column counts must not exceed 8.");
        }
        this.grid = new Piece[row][col];

        // Initialize the grid with null values
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (j == 0) {
                    if (i == 2) {
                        this.grid[i][j] = new Master(1, i, j); // Place Master for player 1
                    } else {
                        this.grid[i][j] = new Pawn(1, i, j); // Place Pawns for player 1
                    }
                } else if (j == col - 1) {
                    if (i == 2) {
                        this.grid[i][j] = new Master(2, i, j); // Place Master for player 2
                    } else {
                        this.grid[i][j] = new Pawn(2, i, j); // Place Pawns for player 2
                    }
                } else
                this.grid[i][j] = null; // Empty cells are initialized to null
            }
        }
    }
    
    public Board() {
        this(5, 5);
    }

    // Copy constructor
    public Board(Board otherBoard) {
        this.columns = otherBoard.columns;
        this.rows = otherBoard.rows;
        Piece p = null;
        // Create a new grid and copy each piece
        this.grid = new Piece[otherBoard.rows][otherBoard.columns];
        for (int i = 0; i < otherBoard.rows; i++) {
            for (int j = 0; j < otherBoard.columns; j++) {
                if ((p = otherBoard.grid[i][j]) != null) {
                    if (p.isMaster()) {
                        this.grid[i][j] = new Master((Master) p);
                    } else if (p.isPawn()) {
                        this.grid[i][j] = new Pawn((Pawn) p);
                    }
                    //  else {
                    //     throw new IllegalArgumentException("Unknown piece type in the board copy.");
                    // }
                } else {
                    this.grid[i][j] = null; // Keep empty cells as null
                }
            }
        }
    }
    /* --- --- --- --- --- */


    /* --------------- */
    /* --- Getters --- */
    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Piece[][] getGrid() {
        return grid;
    }

    public Piece getPieceAt(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= columns) {
            throw new IndexOutOfBoundsException("Invalid row or column index.");
        }
        return grid[row][col];
    }
    /* --- --- --- --- --- */

    /* --------------- */
    /* --- Setters --- */
    public void setPieceAt(int row, int col, Piece piece) {
        if (row < 0 || row >= rows || col < 0 || col >= columns) {
            throw new IndexOutOfBoundsException("Invalid row or column index.");
        }
        grid[row][col] = piece;
    }

    public void setGrid(Piece[][] grid) {
        if (grid.length != rows || grid[0].length != columns) {
            throw new IllegalArgumentException("Grid dimensions do not match the board dimensions.");
        }
        this.grid = grid;
    }
    /* --- --- --- --- --- */

    /* -------------- */
    /* --- Status --- */
    public boolean isEmpty(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= columns) {
            throw new IndexOutOfBoundsException("Invalid row or column index.");
        }
        return grid[row][col] == null;
    }

    public boolean isFull() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j] == null) {
                    return false; // Found an empty cell
                }
            }
        }
        return true; // No empty cells found
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    public boolean isValidPosition(Point position) {
        return isValidPosition(position.x, position.y);
    }
    /* --- --- --- --- --- */


    /* ----------------- */
    /* --- Utilities --- */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        if (columns != board.columns || rows != board.rows) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Piece thisPiece = this.grid[i][j];
                Piece otherPiece = board.grid[i][j];
                if ((thisPiece == null && otherPiece != null) || (thisPiece != null && !thisPiece.equals(otherPiece))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + columns;
        result = 31 * result + rows;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Piece piece = grid[i][j];
                result = 31 * result + (piece != null ? piece.hashCode() : 0);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Board ").append(rows).append("x").append(columns).append(":\n");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Piece piece = grid[i][j];
                if (piece == null) {
                    sb.append("[  ]"); // Empty cell
                } else {
                    sb.append("[" + piece.toString(false) + "]"); // Use the Piece's toString method
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    /* --- --- --- --- --- */
}
