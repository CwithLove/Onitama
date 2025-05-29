package Entity;

import java.awt.*;
import java.util.List;

public class Deck  {
    private static final MoveCard[] DECK_CARDS;

    //  Static block to initialize the deck with move cards
    static {
        DECK_CARDS = new MoveCard[16]; // Initialize the deck with 16 move cards
        generateDeck(); // Populate the deck with move cards
    }

    /**
     * Generates a deck of move cards.
     */
    private static void generateDeck() {
        // Example of adding move cards to the deck
        DECK_CARDS[0]  = new MoveCard("Tiger",    List.of(new Point(0,  2), new Point(0, -1)), 1);
        DECK_CARDS[1]  = new MoveCard("Dragon",   List.of(new Point(-2, 1), new Point(2,  1), new Point(-1, -1), new Point(1, -1)), 2);
        DECK_CARDS[2]  = new MoveCard("Frog",     List.of(new Point(-2, 0), new Point(-1, 1), new Point(1, -1)), 1);
        DECK_CARDS[3]  = new MoveCard("Rabbit",   List.of(new Point(2,  0), new Point(1,  1), new Point(-1, -1)), 2);
        DECK_CARDS[4]  = new MoveCard("Crab",     List.of(new Point(0,  1), new Point(-2, 0), new Point(2,  0)), 1);
        DECK_CARDS[5]  = new MoveCard("Elephant", List.of(new Point(-1, 0), new Point(1,  0), new Point(-1, 1), new Point(1, 1)), 2);
        DECK_CARDS[6]  = new MoveCard("Mantis",   List.of(new Point(0,  1), new Point(-1, -1), new Point(1, -1)), 2);
        DECK_CARDS[7]  = new MoveCard("Boar",     List.of(new Point(0,  1), new Point(-1, 0), new Point(1,  0)), 2);
        DECK_CARDS[8]  = new MoveCard("Goose",    List.of(new Point(-1, 1), new Point(0,  1), new Point(1, 0), new Point(-1, 0)), 1);
        DECK_CARDS[9]  = new MoveCard("Rooster",  List.of(new Point(-1, 1), new Point(0,  1), new Point(1, -1), new Point(1, 0), new Point(-1, 0)), 2);
        DECK_CARDS[10] = new MoveCard("Monkey",   List.of(new Point(-1, 1), new Point(1,  1), new Point(-1, -1), new Point(1, -1)), 1);
        DECK_CARDS[11] = new MoveCard("Horse",    List.of(new Point(0,  1), new Point(1,  0), new Point(0, -1)), 1);
        DECK_CARDS[12] = new MoveCard("Cobra",    List.of(new Point(1,  0), new Point(-1, 1), new Point(-1, -1)), 2);
        DECK_CARDS[13] = new MoveCard("Eel",      List.of(new Point(1,  1), new Point(-1, 1), new Point(0, -1)), 1);
        DECK_CARDS[14] = new MoveCard("Eagle",    List.of(new Point(-1, 1), new Point(1,  1), new Point(-2, -1), new Point(2, -1)), 1);
        DECK_CARDS[15] = new MoveCard("Ox",       List.of(new Point(0,  1), new Point(-1, 0), new Point(0, -1)), 2);
    }

    public static boolean contains(MoveCard moveCard) {
        if (moveCard == null) return false;
        for (MoveCard card : DECK_CARDS) {
            if (card.equals(moveCard)) {
                return true; // Found a matching move card in the deck
            }
        }
        return false; // No matching move card found
    }

    public static MoveCard[] getDeckCards() {
        return DECK_CARDS.clone(); // Return a copy of the deck to prevent external modification
    }

}
