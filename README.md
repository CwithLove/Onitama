# Onitama Game 🎮

## Compile and Launch
### To Compile
``` bash
javac -cp src -d out src/*.java
```
---
### To run
``` bash
java -cp out Onitama
```
---


## Functionnality


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
    * `resetGame()`: Reset all the attributs of gameState.
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

## Package: `Controller` 🕹️

The `Controller` package acts as the intermediary between the `Entity` (model) and `Boundary` (view) packages. It handles user input, updates the game state accordingly, and instructs the view to refresh.

### Class Overview

#### `GameController.java`
* **Purpose:** Manages the overall game flow, processes user interactions, and updates the game state and view.
* **Key Attributes:**
    * `gameState` (GameState): A reference to the current state of the game.
    * `gameView` (GameView): A reference to the UI of the game.
    * `selectedPiece` (Piece): The piece currently selected by the player.
    * `selectedCard` (MoveCard): The move card currently selected by the player.
* **Key Methods:**
    * `setView(GameView)`: Sets the game view.
    * `startGame()`: Initializes and starts a new game by calling `gameState.initializeGameSetup()` and updating the view.
    * `resetGame()`: Resets the game to its initial state and updates the view.
    * `handleCellClick(int row, int col)`: Processes a click on a board cell. This method handles:
        * Selecting a piece if none is selected.
        * Switching to another piece if one is already selected and the click is on another of the player's pieces.
        * Attempting to make a move if a piece and a card are selected, and the click is on a target cell.
        * If a move is successful, it updates the game state, resets selections, and triggers an animation and view update.
    * `handleCardSelection(MoveCard card)`: Processes the selection of a move card by the current player. If a piece is also selected, it shows possible moves.
* **Interaction Flow:**
    1. User interacts with the `GameView` (e.g., clicks a cell or a card).
    2. `GameView` calls the appropriate handler method in `GameController` (e.g., `handleCellClick` or `handleCardSelection`).
    3. `GameController` updates `selectedPiece` and `selectedCard` based on the input.
    4. If a move is attempted, `GameController` calls `gameState.playTurn()`.
    5. `GameState` validates the move and updates the board and player hands.
    6. `GameController` then instructs `GameView` to update its display (e.g., `gameView.updateView()`, `gameView.animateMove()`).

---

## Package: `Boundary` 🖼️

The `Boundary` package is responsible for the graphical user interface (GUI) of the Onitama game. It displays the game state to the user and captures user input, which is then passed to the `Controller`.

### Class Overview

#### `GameView.java` (Extends `JFrame`)
* **Purpose:** The main window of the game, orchestrating all other UI components.
* **Key Components:**
    * `controller` (GameController): Reference to the game controller.
    * `boardPanel` (BoardPanel): Displays the game board and pieces.
    * `playerCardsPanelP1` (PlayerCardsPanel): Displays cards for Player 1.
    * `playerCardsPanelP2` (PlayerCardsPanel): Displays cards for Player 2.
    * `neutralCardPanel` (NeutralCardPanel): Displays the neutral card.
    * `statusLabel` (JLabel): Shows game messages and current turn.
    * `restartButton` (JButton): Allows restarting the game.
* **Layout:** Uses `BorderLayout` to arrange panels: `boardPanel` in the center, player card panels to the West/East, neutral card panel to the North, and status/restart to the South.
* **Key Methods:**
    * `initUI()`: Sets up all UI components and their layout.
    * `updateView()`: Refreshes the entire UI based on the current `GameState` (board, all cards, active player highlight).
    * `updatePlayerCards(Player)`: Updates the card display for a specific player.
    * `updateNeutralCard(MoveCard)`: Updates the neutral card display.
    * `highlightSelectedPiece(Piece)`: Tells `boardPanel` to highlight a selected piece.
    * `highlightSelectedCard(MoveCard)`: Tells the appropriate `PlayerCardsPanel` to highlight a selected card.
    * `showPossibleMoves(ArrayList<Point>)`: Tells `boardPanel` to display possible move locations.
    * `clearHighlightsAndPossibleMoves()`: Clears all visual highlights.
    * `showMessage(String)`: Displays a message on the `statusLabel`.
    * `showGameOver(String)`: Shows a game over dialog and updates the status label.
    * `animateMove(Piece, Point, Point, Runnable)`: Delegates to `boardPanel` to perform piece movement animation.

---

#### `BoardPanel.java` (Extends `JPanel`)
* **Purpose:** Renders the Onitama game board, including cells, pieces, temple arches, selected piece highlights, and possible move indicators. It also handles mouse clicks for piece selection and movement.
* **Key Features:**
    * **Dynamic Sizing:** Calculates `dynamicCellSize`, `dynamicPieceSize`, and offsets (`dynamicOffsetX`, `dynamicOffsetY`) to make the board responsive to panel resizing. This is done in `updateDynamicDimensions()` and called within `paintComponent()`.
    * **Rendering:**
        * Draws checkerboard cells, temple arches for Player 1 (blue) and Player 2 (red).
        * Highlights the currently selected piece and possible move locations.
        * Draws pieces (Masters as ovals, Pawns as rectangles) with 'M' or 'P' identifiers and player-specific colors.
    * **Mouse Input:** A `MouseAdapter` listens for clicks, converts screen coordinates to board coordinates using dynamic dimensions, and calls `controller.handleCellClick(row, col)`.
    * **Animation:**
        * `animateMove(Piece, Point, Point, Runnable)`: Manages a `javax.swing.Timer` to animate a piece moving from a start to an end screen position.
        * `pieceToAnimate`, `animationStartScreenPos`, `animationEndScreenPos`, `animationCurrentScreenPos` store animation state.
        * The piece being animated is not drawn at its static board position during animation; instead, it's drawn at `animationCurrentScreenPos`.
        * A `Runnable onAnimationCompleteCallback` is executed when the animation finishes.
* **Interaction:**
    * Receives board updates via `updateBoard(Board)`.
    * Visual cues set via `setSelectedPiece(Piece)` and `setPossibleMoves(List<Point>)`.

---

#### `CardComponent.java` (Extends `JPanel`)
* **Purpose:** Displays a single `MoveCard`, including its name, a 5x5 grid representing move patterns, and the starting player indicator.
* **Key Features:**
    * **Dynamic Sizing:** `updateDynamicDimensions()` recalculates font sizes, grid cell sizes, and offsets based on the component's current width and height to ensure the card's content scales appropriately.
    * **Rendering (`paintComponent`):**
        * Draws a parchment-like background and border.
        * Renders the card's name, centered at the top.
        * Draws a 5x5 grid. The center square (2,2) is marked black to represent the piece's current position.
        * Possible moves (from `card.getMoves()`) are drawn as blue squares on this grid, relative to the center. The `Point` objects from `MoveCard` store `(deltaRow, deltaCol)`, which are translated to grid coordinates for display.
        * A colored dot in the top-right corner indicates the starting player associated with the card (blue for Player 1, red for Player 2).
    * **Mouse Input:** A `MouseAdapter` listens for clicks. If the card belongs to the current player, it calls `controller.handleCardSelection(this.card)`.
* **Data:** Holds a `MoveCard` object and a reference to the `GameController`.

---

#### `PlayerCardsPanel.java` (Extends `JPanel`)
* **Purpose:** A container panel that displays the two `MoveCard`s currently held by a specific player.
* **Layout:** Uses `GridLayout(0, 1, ...)` to arrange two `CardComponent` instances vertically, one for each card.
* **Key Features:**
    * `updateCards(List<MoveCard>)`: Clears existing card components and creates new `CardComponent` instances for the cards in the provided list. It also attempts to preserve the highlight on a selected card if it's still present.
    * `setSelectedCard(MoveCard)`: Sets the visual highlight (an orange border) on the `CardComponent` that matches the provided `MoveCard`, but only if it's the current player's turn and the card belongs to them. Other cards get a default gray border.
* **Interaction:**
    * Stores references to the `GameController` and the `playerId` it represents.
    * Contains a list of `CardComponent`s to manage their display.

---

#### `NeutralCardPanel.java` (Extends `JPanel`)
* **Purpose:** A simple panel designed to display the single neutral `MoveCard`.
* **Layout:** Uses `BorderLayout` to allow its child `CardComponent` to fill the entire panel.
* **Key Features:**
    * `updateCard(MoveCard)`: Removes any existing card component and creates a new `CardComponent` for the provided neutral card.
    * The border of the `CardComponent` within this panel is colored based on the `starting` player indicated by the neutral card (blue for Player 1, red for Player 2).
* **Note:** The `CardComponent` for the neutral card is created with `controller = null` as it's not directly clickable to be "selected" in the same way player hand cards are.

