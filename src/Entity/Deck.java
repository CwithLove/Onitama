package Entity;

import java.util.Arrays;
import java.util.function.Predicate;

public class Deck <T extends MoveCard> {
    private static MoveCard[] deck = new MoveCard[16];

    public Deck() {
        generateDeck();
    }

    /**
     * Generates a deck of move cards.
     */
    private void generateDeck() {
        // Example of adding move cards to the deck
        deck[0] = new MoveCard("Move 1", );
        deck[1] = new MoveCard("Move 2", );
        // Add more move cards as needed
    }

    /**
     * validates if the move card is valid.
     *
     * @param moveCard The move card to validate.
     * @return True if the move card is valid, false otherwise.
     */
    public static boolean valid(MoveCard moveCard) {
        return Arrays.asList(deck).contains(moveCard); // Move card is not in the deck
    }
}
