package bakery;

import java.io.File;
import java.util.List;
import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Collection;
import java.util.Random;
import java.io.Serializable;
import java.io.IOException;
import java.io.FileNotFoundException;
import bakery.CustomerOrder.CustomerOrderStatus;
import util.CardUtils;

/** Represents all customers.
 * @author Amelie Coy
 * @version 1
 * @since 2024
*/

public class Customers implements Serializable{

    private Collection<CustomerOrder> activeCustomers;
    private Collection<CustomerOrder> customerDeck;
    private List<CustomerOrder> inactiveCustomers;
    private Random random;
    private static final long serialVersionUID = 2L;

    /**
     * Constructs a Customers object.
     *
     * @param deckFile   the file containing customer orders
     * @param random     the random number generator
     * @param layers     the collection of layers available in the game
     * @param numPlayers the number of players in the game
     * @throws FileNotFoundException if the deck file is not found
     */
    public Customers(String deckFile, Random random, Collection<Layer> layers, int numPlayers) throws FileNotFoundException{
        this.random = random;

        this.inactiveCustomers = new ArrayList<>();
        this.activeCustomers = new LinkedList<>();
        this.initialiseCustomerDeck(deckFile, layers, numPlayers);

        this.addActive(null);
        this.addActive(null);
        this.addActive(null);



    }
    /**
     * Adds a customer order to the active customers list.
     *
     * <p>
     * Draws a customer order from the customer deck and adds it to the active customers list.
     * </p>
     *
     * @return the discarded customer order
     */
    public CustomerOrder addCustomerOrder(){

        Stack<CustomerOrder> stack = (Stack) this.customerDeck;


        LinkedList<CustomerOrder> actives = (LinkedList) this.activeCustomers;
        CustomerOrder discarded = this.timePasses();



        actives = (LinkedList) this.activeCustomers;

        try{
            CustomerOrder drawn = stack.pop();
            actives.set(2, drawn);
            if (this.customerWillLeaveSoon()){
                actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            }
        }
        catch (Exception e){
            if (this.customerWillLeaveSoon()){
                actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            }
            throw e;
        }

        //this.activeCustomers = actives;
        //this.customerDeck = stack;
        return discarded;
    }

    /**
     * Checks if the last customer order in the active customers list is impatient.
     *
     * @return {@code true} if the last customer order is impatient, otherwise {@code false}
     */
    public boolean customerWillLeaveSoon(){
        LinkedList<CustomerOrder> actives = (LinkedList) this.activeCustomers;
        CustomerOrder lastOrder = actives.get(0);
        if (lastOrder != null){
            //PUT THIS WHEN IMPATIENT IS SET!! + REMOVE AFTER AN ORDER IS FULFILLED etc
            if (this.customerDeck.isEmpty()){
                CustomerOrder middleOrder = actives.get(1);
                if (middleOrder != null){
                    return true;//last order IS impatient
                }
                else{
                    if (this.size() == 1){
                        return true;
                    }
                }
            }

            else{
                if (this.size() == 3){
                    return true;
                }
            }
        }
        return false;//if emoty, check if something's directly behind it
        //if deck not empty, need size = 3 to push out
    }

    /**
     * Draws a customer order from the customer deck.
     *
     * <p>
     * Draws a customer order from the customer deck.
     * </p>
     *
     * @return the drawn customer order
     */
    public CustomerOrder drawCustomer(){

        Stack<CustomerOrder> stack = (Stack) this.customerDeck;
        CustomerOrder order = stack.pop();
        //this.customerDeck = stack;
        return order;

    }

    /**
     * Retrieves the collection of active customers.
     *
     * @return the collection of active customers
     */
    public Collection<CustomerOrder> getActiveCustomers(){

        return this.activeCustomers;

    }

    /**
     * Retrieves the collection of active customers.
     *
     * @return the collection of active customers
     */
    public Collection<CustomerOrder> getInactiveCustomers(){

        return this.inactiveCustomers;

    }

    /**
     * Retrieves the collection of customer orders in the deck.
     *
     * @return the collection of customer orders in the deck
     */
    public Collection<CustomerOrder> getCustomerDeck(){
        return this.customerDeck;

    }

    /**
     * Retrieves the collection of fulfillable customer orders based on the player's hand.
     *
     * @param hand the player's hand
     * @return the collection of fulfillable customer orders
     */
    public Collection<CustomerOrder> getFulfillable(List<Ingredient> hand){
        Collection<CustomerOrder> fulfillables = new ArrayList<>();
        for (CustomerOrder order: activeCustomers){
            if (order != null){
                if (order.canFulfill(hand)){
                    fulfillables.add(order);
                }
            }

        }
        return fulfillables;
    }

    /**
     * Retrieves the collection of inactive customers with the specified status.
     *
     * @param status the status of the inactive customers to retrieve
     * @return the collection of inactive customers with the specified status
     */
    public Collection<CustomerOrder> getInactiveCustomersWithStatus(CustomerOrderStatus status){
        Collection<CustomerOrder> inactives = new ArrayList<>();;
        for (CustomerOrder order: inactiveCustomers){
            if (order != null){
                if (order.getStatus() == status){
                    inactives.add(order);
                }
            }

        }
        return inactives;

    }

    /**
     * Initialises the customer deck using data from the specified file and layers.
     *
     * @param deckFile   the file containing customer orders
     * @param layers     the collection of layers available in the game
     * @param numPlayers the number of players in the game
     * @throws FileNotFoundException if the deck file is not found
     */
    private void initialiseCustomerDeck(String deckFile, Collection<Layer> layers, int numPlayers) throws FileNotFoundException{
        this.customerDeck = new Stack<>();
        List<CustomerOrder> allOrders = new ArrayList<>();
        try{
            allOrders = CardUtils.readCustomerFile(deckFile, layers);
        }
        catch (IOException e){
            throw new FileNotFoundException();
        }
        Collections.shuffle(allOrders, this.random);

        List<CustomerOrder> orders1 = new ArrayList<>();
        List<CustomerOrder> orders2 = new ArrayList<>();
        List<CustomerOrder> orders3 = new ArrayList<>();

        for (CustomerOrder order: allOrders){
            if (order.getLevel() == 1){
                orders1.add(order);
            }
            else if (order.getLevel() == 2){
                orders2.add(order);
            }
            else if (order.getLevel() == 3){
                orders3.add(order);
            }

        }

        if (numPlayers == 2){
            for (int i = 0;i<4;i++){
                this.customerDeck.add(orders1.get(i));
            }
            for (int i = 0;i<2;i++){
                this.customerDeck.add(orders2.get(i));
            }
            this.customerDeck.add(orders3.get(0));


        }

        else if (numPlayers == 3 || numPlayers == 4){

            this.customerDeck.add(orders1.get(0));
            for (int i = 0;i<2;i++){
                this.customerDeck.add(orders2.get(i));
            }
            for (int i = 0;i<4;i++){
                this.customerDeck.add(orders3.get(i));
            }

        }

        else if (numPlayers == 5){
            this.customerDeck.add(orders2.get(0));

            for (int i = 0;i<6;i++){
                this.customerDeck.add(orders3.get(i));
            }



        }

        Collections.shuffle((List)this.customerDeck, this.random);


    }

    /**
     * Initialises the customer deck using data from the specified file and layers.
     *
     * @return if empty
     */
    public boolean isEmpty(){
        for (CustomerOrder order: this.activeCustomers){
            if (order != null){
                return false;
            }
        }
        return true;
    }

    /**
     * Initialises the customer deck using data from the specified file and layers.
     *
     * @return order
     */
    public CustomerOrder peek(){
        LinkedList<CustomerOrder> orders = (LinkedList) this.activeCustomers;
        return orders.get(0);

    }

    /**
     * Initialises the customer deck using data from the specified file and layers.
     *
     * @param customer to remove
     */
    public void remove(CustomerOrder customer){
        LinkedList<CustomerOrder> actives = (LinkedList) this.activeCustomers;

        actives.set(actives.indexOf(customer), null);
        this.inactiveCustomers.add(customer);
        try{
            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
        }
        catch (Exception e){}
        try{
            actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
        }
        catch (Exception e){}
        try{
            actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
        }
        catch (Exception e){}
        if (this.customerWillLeaveSoon()){
            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
        }



        this.activeCustomers = (Collection) actives;
        //CHECK this elsewhere!!!

    }

    /**
     * Initialises the customer deck using data from the specified file and layers.
     *
     * @return size
     */
    public int size(){
        int count = 0;
        for (CustomerOrder order: this.activeCustomers){
            if (order != null){
                count++;
            }
        }
        return count;
    }

    /**
     * Initialises the customer deck using data from the specified file and layers.
     *
     * @return order gone
     */
    public CustomerOrder timePasses(){
        if (this.customerDeck.isEmpty()){



            LinkedList<CustomerOrder> actives =  (LinkedList) this.activeCustomers;
            CustomerOrder order;
            int startIndex = -1;
            for (int i = 2;i>-1;i--){
                order = actives.get(i);
                if (order != null){
                    startIndex = i;
                    break;
                }
            }
            if (startIndex == 2){
                if (actives.get(1) == null){
                    actives.set(1, actives.get(2));
                    actives.set(2, null);
                    try{
                        actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    try{
                        actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    try{
                        actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    if (this.customerWillLeaveSoon()){
                        actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                    }
                    this.activeCustomers = (Collection) actives;
                }
                else{
                    if (actives.get(0) == null){
                        actives.set(0, actives.get(1));
                        actives.set(1, actives.get(2));
                        actives.set(2, null);
                        try{
                            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        try{
                            actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        try{
                            actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        if (this.customerWillLeaveSoon()){
                            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                        }
                        this.activeCustomers = (Collection) actives;
                    }
                    else{
                        //move all up and change statuses
                        actives.get(0).abandon();
                        this.inactiveCustomers.add(actives.get(0));
                        CustomerOrder toReturn = actives.get(0);
                        actives.set(0, actives.get(1));
                        actives.set(1, actives.get(2));
                        actives.set(2, null);
                        try{
                            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        try{
                            actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        try{
                            actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        if (this.customerWillLeaveSoon()){
                            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                        }
                        this.activeCustomers = (Collection) actives;
                        return toReturn;


                    }

                }

            }
            else if (startIndex == 1){
                if (actives.get(0) == null){
                    actives.set(0, actives.get(1));
                    actives.set(1, null);
                    try{
                        actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    try{
                        actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    try{
                        actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    if (this.customerWillLeaveSoon()){
                        actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                    }
                    this.activeCustomers = (Collection) actives;
                }
                else{
                    //move all up and change statuses
                    actives.get(0).abandon();
                    CustomerOrder toReturn = actives.get(0);
                    this.inactiveCustomers.add(actives.get(0));
                    actives.set(0, actives.get(1));
                    actives.set(1, null);
                    try{
                        actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    try{
                        actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    try{
                        actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    if (this.customerWillLeaveSoon()){
                        actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                    }
                    this.activeCustomers = (Collection) actives;
                    return toReturn;


                }


            }
            else if (startIndex == 0){
                actives.get(0).abandon();
                CustomerOrder toReturn = actives.get(0);
                this.inactiveCustomers.add(actives.get(0));
                actives.set(0, null);
                try{
                    actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                }
                catch (Exception e){}
                try{
                    actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                }
                catch (Exception e){}
                try{
                    actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                }
                catch (Exception e){}
                if (this.customerWillLeaveSoon()){
                    actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                }
                this.activeCustomers = actives;
                return toReturn;

            }



            this.activeCustomers = (Collection) actives;

        }
        else {

            LinkedList<CustomerOrder> actives = (LinkedList) this.activeCustomers;
            if (actives.get(2) == null){
                actives.set(2, null);
                try{
                    actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                }
                catch (Exception e){}
                try{
                    actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                }
                catch (Exception e){}
                try{
                    actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                }
                catch (Exception e){}
                if (this.customerWillLeaveSoon()){
                    actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                }
                this.activeCustomers = (Collection) actives;
            }
            else{
                if (actives.get(1) == null){
                    actives.set(1, actives.get(2));
                    actives.set(2, null);
                    try{
                        actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    try{
                        actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    try{
                        actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                    }
                    catch (Exception e){}
                    if (this.customerWillLeaveSoon()){
                        actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                    }
                    this.activeCustomers = (Collection) actives;
                }
                else{
                    if (actives.get(0) == null){
                        actives.set(0, actives.get(1));
                        actives.set(1, actives.get(2));
                        actives.set(2, null);
                        try{
                            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        try{
                            actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        try{
                            actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        if (this.customerWillLeaveSoon()){
                            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                        }
                        this.activeCustomers = (Collection) actives;
                    }
                    else{
                        //move all up and change statuses
                        actives.get(0).abandon();
                        this.inactiveCustomers.add(actives.get(0));
                        CustomerOrder old = actives.get(0);
                        actives.set(0, actives.get(1));
                        actives.set(1, actives.get(2));
                        actives.set(2, null);
                        try{
                            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        try{
                            actives.get(1).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        try{
                            actives.get(2).setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
                        }
                        catch (Exception e){}
                        if (this.customerWillLeaveSoon()){
                            actives.get(0).setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
                        }
                        this.activeCustomers = (Collection) actives;
                        return old;


                    }

                }
            }

            //when done:

            this.activeCustomers = (Collection) actives;

        }
        return null;
    }

    /**
     * Initialises the customer deck using data from the specified file and layers.
     *
     * @param actives if empty
     */
    public void replaceActiveCustomers(Collection<CustomerOrder> actives){
        this.activeCustomers = actives;

    }

    /**
     * Initialises the customer deck using data from the specified file and layers.
     *
     * @param order if empty
     */
    public void addActive(CustomerOrder order){
        this.activeCustomers.add(order);

    }

    /**
     * Initialises the customer deck using data from the specified file and layers.
     *
     */
    public void reverseActive(){
        LinkedList<CustomerOrder> actives = (LinkedList) this.activeCustomers;
        Collections.reverse(actives);
    }


}

//GOOD MORNING!! Fix when an order turns impatient! ie, if
//there is nothing behind it to push it up, then it is NOT
//impatient, even if it is in the 'last space'

