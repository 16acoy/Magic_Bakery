package bakery;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.io.Serializable;

/** Represents all customer orders.
 * @author Amelie Coy
 * @version 1
 * @since 2024
*/
public class CustomerOrder implements Serializable {
    private List<Ingredient> garnish;
    private int level;
    private String name;
    private List<Ingredient> recipe;
    private CustomerOrderStatus status;
    private static final long serialVersionUID = 5L;

    /** Constructs
     * @param name stuff
     * @param recipe stuff
     * @param garnish stuff
     * @param level stuff
     * @throws WrongIngredientsException lol
    */
    public CustomerOrder(String name, List<Ingredient> recipe, List<Ingredient> garnish, int level) throws WrongIngredientsException{

        if (recipe == null || recipe.size() == 0){
            throw new WrongIngredientsException();
        }

        this.garnish = garnish;
        this.recipe = recipe;
        this.level = level;
        this.name = name;
        this.status = CustomerOrderStatus.WAITING;

    }

    /** Constructs
     *
    */
    public void abandon(){
        this.status = CustomerOrderStatus.GIVEN_UP;
    }

    /** Constructs
     * @param ingredients stuff
     * @return WrongIngredientsException lol
    */
    public boolean canFulfill(List<Ingredient> ingredients){

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
                if (key instanceof Layer){
                    return false;
                    //is a layer
                }
                else{
                    //ingredient only
                    diff += countMapRecipe.get(key);
                }

            }
            else if (countMapHand.get(key) < countMapRecipe.get(key)) {
                if (key instanceof Layer){
                    return false;
                    //is a layer
                }
                else{
                    //ingredient only
                    diff += countMapRecipe.get(key) - countMapHand.get(key);
                }
            }
        }



        if (ducks >= diff){
            return true;
        }

        return false;
    }

    /** Constructs
     * @param ingredients stuff
     * @return level stuff
    */
    public boolean canGarnish(List<Ingredient> ingredients){

        HashMap<Ingredient, Integer> countMapHand = new HashMap<>();
        for (Ingredient item : ingredients) {
            countMapHand.put(item, countMapHand.getOrDefault(item, 0) + 1);
        }

        HashMap<Ingredient, Integer> countMapRecipe = new HashMap<>();
        for (Ingredient item : this.garnish) {
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
                if (key instanceof Layer){
                    return false;
                    //is a layer
                }
                else{
                    //ingredient only
                    diff += countMapRecipe.get(key);
                }

            }
            else if (countMapHand.get(key) < countMapRecipe.get(key)) {
                if (key instanceof Layer){
                    return false;
                    //is a layer
                }
                else{
                    //ingredient only
                    diff += countMapRecipe.get(key) - countMapHand.get(key);
                }
            }
        }

        if (ducks >= diff){
            return true;
        }

        return false;
    }

    /** Constructs
     * @param ingredients stuff
     * @param garnish stuff
     * @return level stuff
    */
    public List<Ingredient> fulfill(List<Ingredient> ingredients, boolean garnish){

        List<Ingredient> ingredientsCopy = new ArrayList<>(ingredients);
        if (this == null){
            return null;
        }
        if (this.canFulfill(ingredientsCopy)){

            List<Ingredient> used = new ArrayList<>();


            HashMap<Ingredient, Integer> countMapHand = new HashMap<>();
            for (Ingredient item : ingredientsCopy) {
                countMapHand.put(item, countMapHand.getOrDefault(item, 0) + 1);
            }

            HashMap<Ingredient, Integer> countMapRecipe = new HashMap<>();
            for (Ingredient item : this.recipe) {
                countMapRecipe.put(item, countMapRecipe.getOrDefault(item, 0) + 1);
            }

            int ducks = 0;
            for (Ingredient ing: ingredientsCopy) {
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
                    //ingredient only
                    diff += countMapRecipe.get(key) - countMapHand.get(key);
                    for (int i = 0;i<countMapHand.get(key);i++){
                        used.add(key);
                    }

                }
                else{
                    for (int i = 0;i<countMapRecipe.get(key);i++){
                        used.add(key);
                    }

                }

            }

            for (int i = 0;i<diff;i++){
                used.add(Ingredient.HELPFUL_DUCK);
            }
            this.status = CustomerOrderStatus.FULFILLED;


            if (garnish == true && !(this.garnish.isEmpty())){
                //check can garnish or not:

                // Create a set to keep track of removed ingredients
                Set<Ingredient> removedIngredients = new HashSet<>();

                // Iterate over the ingredientsCopy list
                Iterator<Ingredient> iterator = ingredientsCopy.iterator();
                while (iterator.hasNext()) {
                    Ingredient ingredient = iterator.next();
                    if (used.contains(ingredient) && !removedIngredients.contains(ingredient)) {
                        iterator.remove(); // Remove the first occurrence
                        removedIngredients.add(ingredient); // Add the removed ingredient to the set
                    }
                }



                List<Ingredient> used2 = new ArrayList<>(used);



                countMapHand.clear();
                for (Ingredient item : ingredientsCopy) {
                    countMapHand.put(item, countMapHand.getOrDefault(item, 0) + 1);
                }

                countMapRecipe.clear();
                for (Ingredient item : this.garnish) {
                    countMapRecipe.put(item, countMapRecipe.getOrDefault(item, 0) + 1);
                }

                ducks = 0;
                for (Ingredient ing: ingredientsCopy) {
                    if (ing.equals(Ingredient.HELPFUL_DUCK)){
                        ducks++;
                    }
                }

                diff = 0;
                for (Ingredient key : countMapRecipe.keySet()) {
                    if (!countMapHand.containsKey(key)){
                        diff += countMapRecipe.get(key);


                    }
                    else if (countMapHand.get(key) < countMapRecipe.get(key)) {
                        //ingredient only
                        diff += countMapRecipe.get(key) - countMapHand.get(key);
                        for (int i = 0;i<countMapHand.get(key);i++){
                            used2.add(key);
                        }

                    }
                    else{
                        for (int i = 0;i<countMapRecipe.get(key);i++){
                            used2.add(key);
                        }

                    }

                }

                if (ducks >= diff){
                    for (int i = 0;i<diff;i++){
                        used2.add(Ingredient.HELPFUL_DUCK);
                    }
                    this.status = CustomerOrderStatus.GARNISHED;
                }
                else{
                    //keep as just fulfilled
                    return used;
                }

                return used2;

            }
            return used;



        }
        throw new WrongIngredientsException();

    }


    /** Constructs
     * @return level stuff
    */
    public List<Ingredient> getGarnish(){
        return this.garnish;
    }

    /** Constructs
     * @return level stuff
    */
    public String getGarnishDescription(){
        String desc = new String();
        for (Ingredient item: this.garnish){
            String ingName = item.toString();
            if (!desc.isEmpty()){
                desc += ", ";
            }
            desc += ingName;
        }
        return desc;
    }

    /** Constructs
     *
     * @return level stuff
    */
    public int getLevel(){
        return this.level;
    }

    /** Constructs
     * @return level stuff
    */
    public List<Ingredient> getRecipe(){
        return this.recipe;
    }

    /** Constructs
     * @return level stuff
    */
    public String getRecipeDescription(){
        String desc = new String();
        for (Ingredient item: this.recipe){
            String ingName = item.toString();
            if (!desc.isEmpty()){
                desc += ", ";
            }
            desc += ingName;
        }
        return desc;
    }

    /** Constructs
     * @return level stuff
    */
    public CustomerOrderStatus getStatus(){
        return this.status;


    }

    /** Constructs
     * @param status stuff
    */
    public void setStatus(CustomerOrderStatus status){
        this.status = status;
    }

    /** Constructs
     * @return level stuff
    */
    public String toString(){
        return this.name;
    }

    /** Constructs
     *
    */
    public enum CustomerOrderStatus{
        WAITING, FULFILLED, GARNISHED, IMPATIENT, GIVEN_UP;

    }


}