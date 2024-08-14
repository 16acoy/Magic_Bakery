package bakery;

import java.util.List;
import java.util.HashSet;
import java.util.Stack;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
import util.CardUtils;
import util.ConsoleUtils;
import util.StringUtils;
import java.lang.IllegalArgumentException;
import java.util.Random;
import java.util.EmptyStackException;
import java.io.Serializable;
import java.io.IOException;

/** Represents the bakery .
 * @author Amelie Coy
 * @version 1
 * @since 2024
*/
public class MagicBakery implements Serializable {

    /** Constructs
     * @param serialVersionUID stuff
    */

    private static final long serialVersionUID = 1L;

    // Fields
    private Collection<Player> players;
    private Random random;
    private Collection<Ingredient> pantry;
    private Collection<Ingredient> pantryDiscard;
    private Collection<Ingredient> pantryDeck;
    private Collection<Layer> layers;
    private Customers customers;
    public int numPlayers;
    public int actionsTaken;
    public int currentPlayerIndex;
    public boolean startFlag = false;
    public boolean safeToChangePlayer = false;

    // Constructors
    /**
     * Constructs a MagicBakery game instance with a given seed for randomness and filenames for ingredient and layer decks.
     *
     * @param seed The seed for randomness.
     * @param ingredientDeckFile The filename of the ingredient deck.
     * @param layerDeckFile The filename of the layer deck.
     * @throws IOException If an I/O error occurs while reading the decks.
     */



    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) throws IOException{
        this.players = new ArrayList<>();
        this.random = new Random(seed);
        this.pantryDiscard = new Stack<>();
        this.pantry = new ArrayList<>();


        this.pantryDeck = CardUtils.readIngredientFile(ingredientDeckFile);


        Stack<Ingredient> stack = (Stack) this.pantryDeck;

        this.layers = CardUtils.readLayerFile(layerDeckFile);

        //List<CustomerOrder> returned3 = CardUtils.readCustomerFile("../../io/customers.csv", CardUtils.readLayerFile("../../io/layers.csv"));






    }

    /**
     * Bakes a layer for the current player if possible.
     *
     * @param layer The layer to bake.
     */

    public void bakeLayer(Layer layer){
        //need to change hand!!
        if (this.getPrevPlayer().getActionsRemaining(this) <= 0 && this.safeToChangePlayer == false && this.getCurrentPlayer().actionsTaken == this.getActionsPermitted()){
            throw new TooManyActionsException();
        }
        else{
            this.safeToChangePlayer = false;
            //func
            List<Ingredient> ingredientsCopy = new ArrayList<>(this.getCurrentPlayer().getHand());
            if (layer.canBake(ingredientsCopy)){

                List<Ingredient> used = new ArrayList<>();

                HashMap<Ingredient, Integer> countMapHand = new HashMap<>();
                for (Ingredient item : ingredientsCopy) {
                    countMapHand.put(item, countMapHand.getOrDefault(item, 0) + 1);
                }

                HashMap<Ingredient, Integer> countMapRecipe = new HashMap<>();
                for (Ingredient item : layer.getRecipe()) {
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
                ArrayList<Ingredient> considered = new ArrayList<>();

                for (Ingredient use: used){
                    if (!considered.contains(use)){
                        this.getCurrentPlayer().removeFromHand(use);
                        considered.add(use);
                        this.pantryDiscard.add(use);
                    }

                }
                this.getCurrentPlayer().addToHand(layer);
                this.layers.remove(layer);
                this.getCurrentPlayer().actionsTaken += 1;

            }
            else{
                throw new WrongIngredientsException();
            }
        }


    }
    /**
     * Draws an ingredient card from the pantry deck.
     *
     * @return The drawn ingredient card.
     */
    private Ingredient drawFromPantryDeck(){

        Stack<Ingredient> stack = (Stack) this.pantryDeck;

        Stack<Ingredient> stack2 = (Stack) this.pantryDiscard;

        Ingredient removed = null;

        try{
            removed = stack.pop();
            this.pantryDeck = stack;

            return removed;
        }
        catch (EmptyStackException e){
            if (this.pantryDiscard.size() != 0){

                this.rePopulatePantryDeck(stack2, stack);}
            else{
                throw new EmptyPantryException("Players must bake with Ingredient cards in their hand", e);
                }

            stack = (Stack) this.pantryDeck;
            removed = stack.pop();
            this.pantryDeck = stack;
            return removed;
        }
    }

    /**
     * Draws an ingredient card from the pantry and adds it to the current player's hand.
     *
     * @param ingredient The ingredient to draw from the pantry.
     */

    public void drawFromPantry(Ingredient ingredient){

        if (this.getPrevPlayer().getActionsRemaining(this) <= 0 && this.safeToChangePlayer == false && this.getCurrentPlayer().actionsTaken == this.getActionsPermitted()){
            throw new TooManyActionsException();
        }
        else{
            this.safeToChangePlayer = false;


            if (this.pantry.contains(ingredient)){
                this.pantry.remove(ingredient);
                this.getCurrentPlayer().addToHand(ingredient);
            }
            else{
                throw new WrongIngredientsException();
            }

            Ingredient new1 = this.drawFromPantryDeck();
            if (new1 != null){
                this.pantry.add(new1);
            }

            this.getCurrentPlayer().actionsTaken += 1;

        }

    }

    /**
     * Draws an ingredient card by name from the pantry and adds it to the current player's hand.
     *
     * @param ingredientName The name of the ingredient to draw from the pantry.
     */

    public void drawFromPantry(String ingredientName){

        if (this.getPrevPlayer().getActionsRemaining(this) <= 0 && this.safeToChangePlayer == false && this.getCurrentPlayer().actionsTaken == this.getActionsPermitted()){
            throw new TooManyActionsException();
        }
        else{
            this.safeToChangePlayer = false;

            Ingredient ingredient = new Ingredient(ingredientName);

            if (this.pantry.contains(ingredient)){
                this.pantry.remove(ingredient);
                this.getCurrentPlayer().addToHand(ingredient);
            }
            else{
                throw new WrongIngredientsException();
            }


            Ingredient new1 = this.drawFromPantryDeck();
            if (new1 != null){
                this.pantry.add(new1);
            }

            this.getCurrentPlayer().actionsTaken += 1;

        }

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @param customer The customer order to fulfill.
     * @param garnish Indicates whether the order should be garnished.
     * @return The list of ingredients used to fulfill the order.
     */

    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish){

        if (this.getPrevPlayer().getActionsRemaining(this) <= 0 && this.safeToChangePlayer == false && this.getCurrentPlayer().actionsTaken == this.getActionsPermitted()){
            throw new TooManyActionsException();
        }
        else{
            this.safeToChangePlayer = false;

            List<Ingredient> used = customer.fulfill(this.getCurrentPlayer().getHand(), garnish);


            //option 1
            ArrayList<Ingredient> considered = new ArrayList<>();

            for (Ingredient use: used){
                //double sponge!!
                //if (!considered.contains(use)){
                    if (use.getClass() == Layer.class){
                        this.layers.add((Layer)use);
                        considered.add(use);
                        this.getCurrentPlayer().removeFromHand(use);
                    }
                    else{
                        this.getCurrentPlayer().removeFromHand(use);
                        considered.add(use);
                        this.pantryDiscard.add(use);
                    }
                //}

            }

            this.getCurrentPlayer().actionsTaken += 1;
            List<Ingredient> drawn = new ArrayList<>();
            for (CustomerOrder order: this.customers.getActiveCustomers()){
                if (order != null){
                    if (order.getStatus() == CustomerOrder.CustomerOrderStatus.FULFILLED || order.getStatus() == CustomerOrder.CustomerOrderStatus.GARNISHED){
                        this.customers.remove(order);

                    }

                    if (order.getStatus() == CustomerOrder.CustomerOrderStatus.GARNISHED){
                        drawn.add(this.drawFromPantryDeck());
                        drawn.add(this.drawFromPantryDeck());
                        this.getCurrentPlayer().addToHand(drawn.get(0));
                        this.getCurrentPlayer().addToHand(drawn.get(1));
                    }

                }

            }
            return drawn;
        }

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return Actions permitted.
     */

    public int getActionsPermitted(){
        if (this.numPlayers <= 3){
            return 3;
        }
        return 2;

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return Actions remaining.
     */
    public int getActionsRemaining(){
        return this.getActionsPermitted() - this.getCurrentPlayer().actionsTaken;

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return Bakeable layers.
     */
    public Collection<Layer> getBakeableLayers(){
        Collection<Ingredient> ingredients = this.getCurrentPlayer().getHand();
        ArrayList<Layer> bakeable = new ArrayList<>();
        for (Layer layer1: this.layers){
            if (layer1.canBake((List)ingredients) && !bakeable.contains(layer1)){
                bakeable.add(layer1);
            }
        }

        return bakeable;

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return current Player.
     */
    public Player getCurrentPlayer(){
        ArrayList<Player> players2 = (ArrayList) players;
        return players2.get(currentPlayerIndex);

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return current Player.
     */
    public Player getPrevPlayer(){
        ArrayList<Player> players2 = (ArrayList) players;
        try{
            return players2.get(currentPlayerIndex-1);
        }
        catch (Exception e){
            return players2.get(this.numPlayers-1);
        }

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return customers
     */
    public Customers getCustomers(){
        return this.customers;
    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return fulfillable customers
     */
    public Collection<CustomerOrder> getFulfillableCustomers(){
        Collection<CustomerOrder> fulfillables = new ArrayList<>();
        for (CustomerOrder order: this.customers.getActiveCustomers()){
            if (order != null){
                if (order.canFulfill(this.getCurrentPlayer().getHand())){
                    fulfillables.add(order);
                }
            }

        }
        return fulfillables;
    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return garnishable customers
     */
    public Collection<CustomerOrder> getGarnishableCustomers(){
        Collection<CustomerOrder> garnishables = new ArrayList<>();
        for (CustomerOrder order: this.customers.getActiveCustomers()){
            if (order.canGarnish(this.getCurrentPlayer().getHand())){
                if (order.canFulfill(this.getCurrentPlayer().getHand())){
                    garnishables.add(order);
                }
            }
        }
        return garnishables;
    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return layers
     */
    public Collection<Layer> getLayers(){

        ArrayList<Layer> layers1 = (ArrayList) this.layers;
        HashSet<Layer> set = new HashSet<>();
        List<Layer> result = new ArrayList<>();

        // Iterate through the original list
        for (Layer item : layers1) {
            // Add the item to the result list if it's not already present in the set
            if (!set.contains(item)) {
                result.add(item);
                set.add(item);
            }
        }

        return (Collection) result;
    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return pantry
     */
    public Collection<Ingredient> getPantry(){
        return this.pantry;
    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return current Player.
     */
    public Collection<Ingredient> getPantryDeck(){
        return this.pantryDeck;
    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return current Player.
     */
    public Collection<Ingredient> getPantryDiscard(){
        return this.pantryDiscard;
    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return PLAYERS  .
     */
    public Collection<Player> getPlayers(){
        return this.players;
    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @param file the file .
     * @return bakery.
     * @throws IOException if bla bla
     * @throws ClassNotFoundException if bla bla
     */
    public static MagicBakery loadState(File file) throws IOException, ClassNotFoundException{
        try (FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            return (MagicBakery) objectIn.readObject();
        }


    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @return ented.
     */
    public boolean endTurn(){

        //THIS needs to trigger the change of player itself AND ONLY call from driver or smth idk

        this.safeToChangePlayer = true;
        this.getCurrentPlayer().actionsTaken = this.getActionsPermitted();

        //remiain = 0
        this.startFlag = true;

        if (this.getCurrentPlayer().getActionsRemaining(this) == 0){
            this.setNextPlayer();
        }



        if (this.currentPlayerIndex == 0 && this.startFlag == true){

            if (this.customers.getCustomerDeck().isEmpty()){
                CustomerOrder discarded = this.customers.timePasses();
                try{
                    this.customers.remove(discarded);
                }
                catch (Exception e){
                    //none
                }
            }
            else{
                CustomerOrder discarded = this.customers.addCustomerOrder();
                try{
                    this.customers.remove(discarded);
                }
                catch (Exception e){
                    //none
                }
            }


        }
        return !(this.customers.isEmpty());


    }


    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     *
     */
    public void setNextPlayer(){
        this.currentPlayerIndex += 1;
        if (this.currentPlayerIndex == this.numPlayers){
            this.currentPlayerIndex = 0;
        }
        this.getCurrentPlayer().actionsTaken = 0;


    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @param ingredient the file .
     * @param recipient the file .
     */
    public void passCard(Ingredient ingredient, Player recipient){


        if (this.getPrevPlayer().getActionsRemaining(this) <= 0 && this.safeToChangePlayer == false && this.getCurrentPlayer().actionsTaken == this.getActionsPermitted()){
            throw new TooManyActionsException();
        }
        else{
            this.safeToChangePlayer = false;
            if (this.getCurrentPlayer().hasIngredient(ingredient)){
                recipient.addToHand(ingredient);
                this.getCurrentPlayer().removeFromHand(ingredient);
            }
            else{
                throw new WrongIngredientsException();
            }
            this.getCurrentPlayer().actionsTaken += 1;


        }


    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     */
    public void printCustomerServiceRecord(){
        System.out.println("Happy customers eating baked goods: " + (this.customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.FULFILLED).size() + this.customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GARNISHED).size()) + " (" + this.customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GARNISHED).size() + " garnished)");
        System.out.println("Gone to Greggs instead: " + this.customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GIVEN_UP).size());

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     */

    public void printGameState(){
        System.out.println("Layers:");
        System.out.println(this.getLayers());
        System.out.println(StringUtils.layersToStrings(this.getLayers()));
        System.out.println("Pantry:");
        System.out.println(StringUtils.ingredientsToStrings(this.pantry));
        System.out.println("Waiting for service:");
        System.out.println(this.customers.getActiveCustomers());
        System.out.println(StringUtils.customerOrdersToStrings(this.customers.getActiveCustomers()));
        System.out.println();
        System.out.println(this.getCurrentPlayer() + " it's your turn. Your hand contains:");
        System.out.println(this.getCurrentPlayer().getHandStr());
        System.out.println("You have " + this.getCurrentPlayer().getActionsRemaining(this) + " actions remaining.");


    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *

     */
    public void refreshPantry(){

        if (this.getPrevPlayer().getActionsRemaining(this) <= 0 && this.safeToChangePlayer == false && this.getCurrentPlayer().actionsTaken == this.getActionsPermitted()){
            throw new TooManyActionsException();
        }
        else{
            this.safeToChangePlayer = false;
            Stack<Ingredient> stack = (Stack) this.pantryDeck;
            //stack.addAll(this.pantryDeck);
            //this.pantryDeck = stack;

            Stack<Ingredient> stack2 = (Stack)this.pantryDiscard;
            //this.pantryDiscard = stack2;

            ArrayList<Ingredient> pantry1 = (ArrayList) this.pantry;
            //this.pantry = pantry1;

            for (Ingredient ing: pantry1){
                stack2.push(ing);
            }
            pantry1.clear();


            for (int i = 0;i<5;i++){
                try{
                    pantry1.add(stack.pop());
                }
                catch (EmptyStackException e){
                    if (stack2.size() != 0){
                        this.rePopulatePantryDeck(stack2, stack);
                        stack2 = (Stack) this.pantryDiscard;
                        pantry1 = (ArrayList) this.pantry;
                        stack = (Stack) this.pantryDeck;
                        pantry1.add(stack.pop());

                        }
                    else{
                        //this shouldn't happen here
                        throw new EmptyPantryException("NO! Players must bake with Ingredient cards in their hand", e);}
                }
            }
            this.pantry = pantry1;
            this.pantryDeck = stack;
            this.pantryDiscard = stack2;
            this.getCurrentPlayer().actionsTaken += 1;


        }

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @param file the file .
     * @throws IOException the file .
     */
    public void saveState(File file) throws IOException{
        try (FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            // Write the object to the file
            objectOut.writeObject(this);
        }




    }


    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @param pantryDiscard1 the file .
     * @param pantryDeck1 the file .
     */
    private void rePopulatePantryDeck(Stack<Ingredient> pantryDiscard1, Stack<Ingredient> pantryDeck1){
        int size = pantryDiscard1.size();
        for (int n = 0;n<size;n++){
            pantryDeck1.push(pantryDiscard1.pop());
        }



        Collections.shuffle((List)this.pantryDeck, this.random);//NOT WORKING RIGHT - something to do with the casting + shuffle
        this.pantryDeck = pantryDeck1;
        this.pantryDiscard = pantryDiscard1;

    }

    /**
     * Fulfills a customer order, removing used ingredients from the current player's hand and updating the customer status.
     *
     * @param playerNames the file .
     * @param customerDeckFile the file .
     * @throws IOException idk
     */

    public void startGame(List<String> playerNames, String customerDeckFile) throws IOException{


        if (playerNames.size() < 2 || playerNames.size() > 5){
            throw new IllegalArgumentException("Invalid number of players");
        }

        for (String name: playerNames){
            Player player = new Player(name);
            this.players.add(player);
            }
        this.currentPlayerIndex = 0;
        this.numPlayers = playerNames.size();


        this.customers = new Customers(customerDeckFile, this.random, this.layers, this.numPlayers);

        Collections.shuffle((List)this.pantryDeck, this.random);

        for (int i = 0;i<5;i++){

            this.pantry.add(this.drawFromPantryDeck());
        }


        if (numPlayers == 2 || numPlayers == 4){
            this.customers.addCustomerOrder();

        }
        else{
            this.customers.addCustomerOrder();
            this.customers.addCustomerOrder();
        }


        Stack<Ingredient> stack = (Stack) this.pantryDeck;
        //this.pantryDeck = stack;
        for (Player player: this.players){
            for (int i = 0;i<3;i++){
                player.addToHand(stack.pop());
            }
        }
        this.getCurrentPlayer().actionsTaken = 0;

    }

    /**
     * Represents the types of actions that can be performed by players in the MagicBakery game.
     *
     */
    public enum ActionType {
        /**
         * Indicates the action of drawing an ingredient card from the pantry.
         */
        DRAW_INGREDIENT,

        /**
         * Indicates the action of passing an ingredient card to another player.
         */
        PASS_INGREDIENT,

        /**
         * Indicates the action of baking a layer.
         */
        BAKE_LAYER,

        /**
         * Indicates the action of fulfilling a customer order.
         */
        FULFIL_ORDER,

        /**
         * Indicates the action of refreshing the pantry by drawing new ingredients.
         */
        REFRESH_PANTRY;
    }



}