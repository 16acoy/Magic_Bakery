package bakery;

/** Take input from user.
 * @author Amelie Coy
 * @version 1
 * @since 2024
*/
public class WrongIngredientsException extends IllegalArgumentException {

    /**
     * Constructs
     */

    public WrongIngredientsException() {
        super();
    }

    /**
     * Constructs a MagicBakery game instance with a given seed for randomness and filenames for ingredient and layer decks.
     *
     * @param message The seed for randomness.
     */

    public WrongIngredientsException(String message) {
        super(message);
    }
}