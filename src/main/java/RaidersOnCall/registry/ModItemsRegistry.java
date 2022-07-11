package RaidersOnCall.registry;

import RaidersOnCall.items.consumables.SummonRaidScrollItem;
import necesse.engine.registries.ItemRegistry;

public class ModItemsRegistry {
    public static void RegisterAll() {
        /* Register everything here! */
        ItemRegistry.registerItem("summonraidscroll", new SummonRaidScrollItem(), 30.0F, true);
    }
}
