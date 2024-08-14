package bakery;
import java.lang.RuntimeException;

/** Represents the exception .
 * @author Amelie Coy
 * @version 1
 * @since 2024
*/
public class EmptyPantryException extends RuntimeException {

    /**
     * Constructs a MagicBakery game instance with a given seed for randomness and filenames for ingredient and layer decks.
     *
     * @param msg The seed for randomness.
     * @param e The filename of the ingredient deck.
     */
    public EmptyPantryException(String msg, Throwable e) {
        super(msg, e);

    }

}