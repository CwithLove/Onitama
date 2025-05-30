# Onitama Game 🎮

## Package: `Entity` 🧩

The `Entity` package serves as the core of the Onitama game, containing all fundamental data objects and basic logic related to the game's components. It defines the structure of the board, pieces, movement cards, players, and the overall game state.

### Class Overview

Below is a detailed description of each class within the `Entity` package:

---

#### `TypePiece.java` (Enum) 🏷️
* **Purpose:** Defines the possible types of game pieces.
* **Values:**
    * `PAWN`: Represents a Pawn piece ♟️.
    * `MASTER`: Represents a Master piece 👑.

---

#### `Piece.java` (Abstract Class) 🧱
* **Purpose:** An abstract base class for all game pieces. It defines common attributes and behaviors that a piece possesses.
* **Key Attributes:**
    * `playerId` (int): The ID of the player who owns the piece.
    * `position` (Point): The current position of the piece on the board (x, y coordinates) 📍.
    * `type` (TypePiece): The type of the piece (Pawn or Master).
* **Key Methods:**
    * Getters and setters for attributes.
    * `isBelongTo(Player/int)`: Checks if the piece belongs to a specific player.
    * `isMaster()`: Returns `true` if it's a Master piece.
    * `isPawn()`: Returns `true` if it's a Pawn piece.
    * `equals()`, `hashCode()`: Overridden for comparing and hashing Piece objects.
    * `toString()`: Represents the piece as a string (e.g., "M1", "P2").

---

#### `Master.java` (Extends `Piece`) 👑
* **Purpose:** Represents the Master piece.
* **Characteristics:**
    * Inherits from `Piece`.
    * Automatically assigns `TypePiece.MASTER` upon initialization.
    * No additional special methods or attributes beyond those inherited from `Piece`.

---

#### `Pawn.java` (Extends `Piece`) ♟️
* **Purpose:** Represents the Pawn piece.
* **Characteristics:**
    * Inherits from `Piece`.
    * Automatically assigns `TypePiece.PAWN` upon initialization.
    * No additional special methods or attributes beyond those inherited from `Piece`.

---

#### `MoveCard.java` 🃏
* **Purpose:** Represents a movement card, defining possible moves.
* **Key Attributes:**
    * `cardName` (String): The name of the card (e.g., "Tiger", "Dragon").
    * `moves` (List<Point>): A list of relative movement vectors from the piece's current position. Each `Point` in the list signifies a change (dx, dy).
    * `starting` (int): Indicates which player (1 or 2) would start if this card were the initial neutral card.
* **Key Methods:**
    * Getters for attributes.
    * `validPosition(Piece, Board, Point)`: Checks if a move from a piece's position to a target position is valid according to this card (this logic seems to be handled directly in `GameState` in the current implementation rather than being fully utilized here).
    * `equals()`, `hashCode()`: Overridden for comparing and hashing MoveCard objects.
    * `toString()`: Represents the card as a string, including its name and moves.

---

#### `Deck.java` 📚
* **Purpose:** Manages the standard set of 16 movement cards for the Onitama game.
* **Characteristics:**
    * Contains a static array `DECK_CARDS` of 16 predefined `MoveCard`s.
    * `generateDeck()`: A static method (in a static block) to initialize these cards.
    * `getDeckCards()`: Returns a copy of the `DECK_CARDS` array to prevent external modification.
    * `contains(MoveCard)`: Checks if a specific card exists in the standard deck.
* **Note:** This class acts as a provider of the fixed set of cards for the game.

---

#### `Player.java` 👤
* **Purpose:** Represents a player in the game.
* **Key Attributes:**
    * `id` (int): The player's ID (usually 1 or 2).
    * `moveCards` (ArrayArrayArrayList<MoveCard>): The list of movement cards the player currently holds (maximum of 2).
* **Key Methods:**
    * Getters and setters for attributes.
    * `addMoveCard(MoveCard)`: Adds a card to the player's hand (if they have less than 2).
    * `removeMoveCard(MoveCard)`: Removes a card from the player's hand.
    * `hasMoveCard(MoveCard)`: Checks if the player possesses a specific card.
    * `exchangeMoveCard(MoveCard oldCard, MoveCard newCard)`: Swaps an old card for a new one (typically used when playing a card and taking the neutral card).
    * `toString()`: Represents the player and their current cards as a string.

---

#### `Board.java` 🎲
* **Purpose:** Represents the game board.
* **Key Attributes:**
    * `columns` (int): The number of columns on the board.
    * `rows` (int): The number of rows on the board.
    * `grid` (Piece[][]): A 2D array storing `Piece` objects (or `null` for empty squares).
* **Key Methods:**
    * Constructors:
        * `Board(int row, int col)`: Creates a board with custom dimensions and automatically places initial pieces (Masters in the middle row, Pawns in other rows, in the first and last columns for Player 1 and Player 2 respectively).
        * `Board()`: Creates a default 5x5 board.
        * `Board(Board otherBoard)`: Copy constructor.
    * Getters (`getColumns()`, `getRows()`, `getGrid()`, `getPieceAt(int/Point)`).
    * Setters (`setPieceAt(int/Point, Piece)`, `setGrid(Piece[][])`).
    * `getPiecesForPlayer(int playerId)`: Retrieves a list of all pieces belonging to a player.
    * `getMasterForPlayer(int playerId)`: Retrieves a player's Master piece.
    * Board status checks: `isEmpty(int, int)`, `isFull()`, `isValidPosition(int/Point)`.
    * `equals()`, `hashCode()`: Overridden for comparing and hashing Board objects.
    * `toString()`: Represents the board as a string, with an option to highlight possible moves.

---

#### `GameState.java` 🧠⚙️
* **Purpose:** The central class managing the entire state and logic of an Onitama game session.
* **Key Attributes:**
    * `board` (Board): The current game board.
    * `player1` (Player): Player 1.
    * `player2` (Player): Player 2.
    * `neutralCardMove` (MoveCard): The neutral card (the 5th card).
    * `currentPlayerId` (int): The ID of the player whose turn it is.
    * `currentPiece` (Piece): The piece the current player has selected to move (if any).
    * `gameStatus` (String): The status of the game (e.g., "ongoing", "Player 1 wins...").
* **Key Methods:**
    * Constructors (default and copy).
    * Getters and setters for attributes.
    * `checkWinConditions()`: Checks if any player has won (Way of the Stone - capturing opponent's Master; Way of the Stream - own Master reaching opponent's Temple Arch).
    * `isGameOver()`: Returns `true` if the game has ended.
    * `getCurrentPlayer()`, `getOpponentPlayer()`: Retrieves the current player and opponent objects.
    * `getPossibleMoves(...)`: Calculates valid moves for a player (based on all pieces and cards) or for a specific piece and card.
    * `initializeGameSetup()`: Initializes the game: shuffles the deck, deals 2 cards to each player, selects 1 neutral card, and determines the starting player based on the neutral card.
    * `playTurn(Piece, MoveCard, Point)`: Executes a game turn:
        * Validates the move.
        * Updates the piece's position on the board.
        * Exchanges the played card with the neutral card.
        * Checks win conditions.
        * Switches to the next player (if the game is not over).
    * `switchPlayerTurn()`: Switches the turn to the next player.
    * `startGame()`: Starts a console-based game loop allowing users to input moves to play.
    * `toString()`: Represents the entire game state as a string.
* **Note:** This class is the "brain" of the game, coordinating the actions of other entities.

---
