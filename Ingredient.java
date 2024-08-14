package bakery;

import java.lang.Comparable;
import java.io.Serializable;

/** Represents all ingredients.
 * @author Amelie Coy
 * @version 1
 * @since 2024
*/
public class Ingredient implements Comparable, Serializable{

    /** Constructs
     * @param name stuff
    */
    private String name;

    /**
     * Represents a helpful duck ingredient.
     *
     * This constant defines an Ingredient object representing a helpful duck.
     * The Unicode character "𓅭" is included in the name to add uniqueness.
     *
     * @since 1.0
     */
    public static final Ingredient HELPFUL_DUCK = new Ingredient ("helpful duck 𓅭");

    /** Constructs
     * @param serialVersionUID stuff
    */
    private static final long serialVersionUID = 6L;

    /** Constructs
     * @param name stuff
    */
    public Ingredient(String name){
        this.name = name;
    }

    /** Constructs
     * @param o stuff
     * @return level stuff
    */
    public boolean equals(Object o){
        if (o == null) {
            return false;
        }
        if (this.name.equals(o.toString())){
            return true;
        }
        else{
            return false;
        }
    }

    /** Constructs
     * @return level stuff
    */
    public int hashCode(){
        return this.name.hashCode();
    }

    /** Constructs
     * @return level stuff
    */
    public String toString(){
        return this.name;
    }

    /** Constructs
     * @param o stuff
     * @return level stuff
    */
    public int compareTo(Ingredient o) {
        if (o == null) {
            return 0;//wrong
        }
        for (int i = 0; i < Math.min(this.name.length(), ((Ingredient)o).name.length()); i++) {
            int asciiThis = (int) this.name.charAt(i);
            int asciiOther = (int) ((Ingredient)o).name.charAt(i);

            if (asciiThis != asciiOther) {
                return Integer.compare(asciiThis, asciiOther);
            }
        }

        // If names are equal up to the length of the shortest name, compare lengths
        return Integer.compare(this.name.length(), ((Ingredient)o).name.length());
    }


    /** Constructs
     * @param o stuff
     * @return level stuff
    */
    @Override
    public int compareTo(Object o) {
        return this.toString().compareTo(o.toString());
    }


}

