# Magic Bakery Game 

**Overview**

Magic Bakery is a strategic card game where players manage ingredients and layers to fulfill and garnish customer orders. The game involves drawing ingredients, passing cards, and baking layers.

**Core Classes**

CustomerOrderStatus Enum:

WAITING, FULFILLED, GARNISHED, IMPATIENT, ABANDONED

ActionType Enum:

DRAW_INGREDIENT, PASS_INGREDIENT, BAKE_LAYER, FULFILL_ORDER, REFRESH_PANTRY

Ingredient Class: Represents ingredients with a name.

Layer Class: Represents layers with a name and a recipe of ingredients.

**Key Methods**

fulfill(List<Ingredient> ingredients, boolean garnish): Fulfills and optionally garnishes an order using the provided ingredients.

canFulfill(List<Ingredient> ingredients): Checks if the ingredients can fulfill the order.

canGarnish(List<Ingredient> ingredients): Checks if the ingredients can garnish the order.

**Sorting and Equality**

Ingredients and Layers are sorted and compared based on ASCII values of their names. The hashCode method should be derived from the name (Ingredients) or name and recipe (Layers).

**Exceptions**

WrongIngredientsException: Thrown for invalid ingredient or layer states or operations.

EmptyPantryException: Thrown when the pantry is empty and actions cannot be performed.

TooManyActionsException: Thrown if a player exceeds the allowed number of actions.

**Utility Classes**

CardUtils: For reading and parsing card data from files.

ConsoleUtils: For console input and interaction.

StringUtils: For string manipulation and formatting.

**Code Organization**

Code organised into packages: util for helper classes, bakery for game-specific classes.

Public and protected classes and methods documented using Javadoc.
