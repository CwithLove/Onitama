package Entity;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Deck  {
    private static final MoveCard[] deck = new MoveCard[16];

    public Deck() {
        generateDeck();
    }

    /**
     * Generates a deck of move cards.
     */
    private void generateDeck() {
        // Example of adding move cards to the deck
        deck[0]  = new MoveCard("Tiger",    List.of(new Point(0,  2), new Point(0, -1)));
        deck[1]  = new MoveCard("Dragon",   List.of(new Point(-2, 1), new Point(2,  1), new Point(-1, -1), new Point(1, -1)));
        deck[2]  = new MoveCard("Frog",     List.of(new Point(-2, 0), new Point(-1, 1), new Point(1, -1)));
        deck[3]  = new MoveCard("Rabbit",   List.of(new Point(2,  0), new Point(1,  1), new Point(-1, -1)));
        deck[4]  = new MoveCard("Crab",     List.of(new Point(0,  1), new Point(-2, 0), new Point(2,  0)));
        deck[5]  = new MoveCard("Elephant", List.of(new Point(-1, 0), new Point(1,  0), new Point(-1, 1), new Point(1, 1)));
        deck[6]  = new MoveCard("Mantis",   List.of(new Point(0,  1), new Point(-1, -1), new Point(1, -1)));
        deck[7]  = new MoveCard("Boar",     List.of(new Point(0,  1), new Point(-1, 0), new Point(1,  0)));
        deck[8]  = new MoveCard("Goose",    List.of(new Point(-1, 1), new Point(0,  1), new Point(1, 0), new Point(-1, 0)));
        deck[9]  = new MoveCard("Rooster",  List.of(new Point(-1, 1), new Point(0,  1), new Point(1, -1), new Point(1, 0), new Point(-1, 0)));
        deck[10] = new MoveCard("Monkey",   List.of(new Point(-1, 1), new Point(1,  1), new Point(-1, -1), new Point(1, -1)));
        deck[11] = new MoveCard("Horse",    List.of(new Point(0,  1), new Point(1,  0), new Point(0, -1)));
        deck[12] = new MoveCard("Cobra",    List.of(new Point(1,  0), new Point(-1, 1), new Point(-1, -1)));
        deck[13] = new MoveCard("Eel",      List.of(new Point(1,  1), new Point(-1, 1), new Point(0, -1)));
        deck[14] = new MoveCard("Eagle",    List.of(new Point(-1, 1), new Point(1,  1), new Point(-2, -1), new Point(2, -1)));
        deck[15] = new MoveCard("Ox",       List.of(new Point(0,  1), new Point(-1, 0), new Point(0, -1)));
    }

    /**
     * validates if the move card is valid.
     * @param moveCard The move card to validate.
     * @return True if the move card is valid, false otherwise.
     */
    public static boolean valid(MoveCard moveCard) {
        return Arrays.asList(deck).contains(moveCard); // Move card is not in the deck
    }
}
