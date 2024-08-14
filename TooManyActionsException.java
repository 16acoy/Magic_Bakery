package bakery;

/** Represents the exception .
 * @author Amelie Coy
 * @version 1
 * @since 2024
*/
public class TooManyActionsException extends IllegalStateException {

    /**
     * Constructs
     *
     */
    public TooManyActionsException() {
        super();

    }

    /**
     * Constructs
     * @param message stuff
     */

    public TooManyActionsException(String message) {
        super(message);
    }

}