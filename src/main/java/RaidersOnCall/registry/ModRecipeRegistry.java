package RaidersOnCall.registry;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

public class ModRecipeRegistry {
    public static void RegisterAll() {
        Recipes.registerModRecipe(new Recipe("summonraidscroll", RecipeTechRegistry.NONE, new Ingredient[] {
                new Ingredient("coin", 200)
        }));
    }
}
