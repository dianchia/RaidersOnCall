package ChangeThisName;

import ChangeThisName.registry.*;
import necesse.engine.modLoader.annotations.ModEntry;

/**
 *  Entry point for your mod, you should rarely have to do anything in here. All registrations are setup in /registry
 */
@ModEntry
public class ModEntrypoint {

    public void init() {
        ModTileRegistry.RegisterAll();
        ModObjectRegistry.RegisterAll();
        ModBiomeRegistry.RegisterAll();
        ModBuffRegistry.RegisterAll();
        ModItemsRegistry.RegisterAll();
        ModMobsRegistry.RegisterAll();
        ModLevelRegistry.RegisterAll();
        ModContainerRegistry.RegisterAll();
        ModPacketRegistry.RegisterAll();
        ModQuestRegistry.RegisterAll();
    }

    // Load resources such as textures and music.
    public void initResources() {
        ModTextureRegistry.RegisterAll();
    }

    public void postInit() {
        ModRecipeRegistry.RegisterAll();
        ModMobsRegistry.RegisterBiomes();

        ModCommandsRegistry.RegisterAll();
    }

}
