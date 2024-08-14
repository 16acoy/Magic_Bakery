package bakery;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

/** Represents all layers.
 * @author Amelie Coy
 * @version 1
 * @since 2024
*/
public class Layer extends Ingredient implements Serializable {

    private List<Ingredient> recipe;
    private static final long serialVersionUID = 4L;

    /** Constructs
     * @param name stuff
     * @param recipe stuff
     * @throws WrongIngredientsException lol
    */
    public Layer(String name, List<Ingredient> recipe)  throws WrongIngredientsException{
        super(name);
        if (recipe == null || recipe.size() == 0){
            throw new WrongIngredientsException();
        }

        this.recipe = recipe;
    }

    /** Constructs
     * @param ingredients stuff
     * @return recipe stuff
    */
    public boolean canBake(List<Ingredient> ingredients){

        HashMap<Ingredient, Integer> countMapHand = new HashMap<>();
        for (Ingredient item : ingredients) {
            countMapHand.put(item, countMapHand.getOrDefault(item, 0) + 1);
        }


        HashMap<Ingredient, Integer> countMapRecipe = new HashMap<>();
        for (Ingredient item : this.recipe) {
            countMapRecipe.put(item, countMapRecipe.getOrDefault(item, 0) + 1);
        }

        int ducks = 0;
        for (Ingredient ing: ingredients) {
            if (ing.equals(Ingredient.HELPFUL_DUCK)){
                ducks++;
            }
        }

        int diff = 0;
        for (Ingredient key : countMapRecipe.keySet()) {
            if (!countMapHand.containsKey(key)){
                diff += countMapRecipe.get(key);
            }
            else if (countMapHand.get(key) < countMapRecipe.get(key)) {
                diff += countMapRecipe.get(key) - countMapHand.get(key);
            }
        }

        if (ducks >= diff){
            return true;
        }

        return false;

    }


    /** Constructs
     * @return recipe stuff
    */
    public List<Ingredient> getRecipe(){
        return this.recipe;
    }

    /** Constructs
     * @return name stuff
    */
    public String getRecipeDescription(){
        String desc = new String();
        for (Ingredient item: this.recipe){
            String ingName = item.toString();
            if (!desc.isEmpty()){
                desc += ", ";
            }
            desc += ingName.strip();
        }
        return desc;
    }

    /** Constructs
     * @return name stuff
     *
    */
    public int hashCode(){
        return super.hashCode();
    }



}