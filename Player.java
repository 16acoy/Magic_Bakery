package bakery;

import java.util.List;
import java.util.ArrayList;
import util.CardUtils;
import java.util.Comparator;
import java.util.Collection;
import java.io.Serializable;
import util.StringUtils;

/** Represents all players.
 * @author Amelie Coy
 * @version 1
 * @since 2024
*/
public class Player implements Serializable{

    private List<Ingredient> hand;
    private String name;
    private static final long serialVersionUID = 3L;

    /**
     * Number of actions taken by the player.
     */
    public int actionsTaken = 3;

    /** Constructs
     * @param name stuff
    */
    public Player(String name){
        this.name = name;
        this.hand = new ArrayList<Ingredient>();

    }

    /** Constructs
     * @param bakery stuff
     * @return more
    */
    public int getActionsRemaining(MagicBakery bakery){
        return bakery.getActionsPermitted() - this.actionsTaken;

    }

    /** Constructs
     * @param ingredients stuff
    */
    public void addToHand(List<Ingredient> ingredients){
        this.hand.addAll(ingredients);

    }

    /** Constructs
     * @param ingredient stuff
    */
    public void addToHand(Ingredient ingredient){
        this.hand.add(ingredient);

    }

    /** Constructs
     * @param ingredient stuff
     * @return more
    */
    public boolean hasIngredient(Ingredient ingredient){
        return this.hand.contains(ingredient);

    }

    /** Constructs
     * @param ingredient stuff
    */
    public void removeFromHand(Ingredient ingredient){
        if (this.hand.contains(ingredient)){
            this.hand.remove(ingredient);
        }
        else{
            throw new WrongIngredientsException();
        }

    }

    /** Constructs
     * @param ingredient stuff
    */
    public void setHand(List<Ingredient> ingredients){
        this.hand = ingredients;

    }

    /** Constructs
     * @return more
    */
    public List<Ingredient> getHand(){
        this.hand.sort(Comparator.comparing(Ingredient::toString));
        return this.hand;
    }

    /** Constructs
     * @return more
    */
    public String getHandStr(){
        List<Ingredient> hand1 = new ArrayList<Ingredient>();
        hand1 = this.getHand();

        ArrayList<Integer> nums = new ArrayList<>();
        ArrayList<String> ings = new ArrayList<>();

        for (Ingredient item: this.hand){
            String ingName = item.toString();
            if (ings.contains(ingName)){
                int index = ings.indexOf(ingName);
                int num = nums.get(index);
                num += 1;
                nums.set(index, num);
            }
            else{
                ings.add(ingName);
                nums.add(1);
            }
        }

        String desc = new String();
        for (int n =0;n<ings.size();n++){
            int num = nums.get(n);
            String ingName = ings.get(n);
            if (!desc.isEmpty()){
                desc += ", ";
            }
            desc += StringUtils.toTitleCase(ingName);
            if (num > 1){
                desc += " (x" + num + ")";
            }
        }
        return desc;
    }


    /** Constructs
     * @return level stuff
    */
    public String toString(){
        return this.name;

    }



}