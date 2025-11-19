package RaidersOnCall.items.consumables;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.levelEvent.settlementRaidEvent.HumanSettlementRaidLevelEvent;
import necesse.entity.manager.EntityManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

import java.awt.geom.Line2D;

public class SummonRaidScrollItem extends ConsumableItem {
    public SummonRaidScrollItem() {
        super(1, true);
        this.itemCooldownTime.setBaseValue(2000);
        this.dropsAsMatDeathPenalty = false;
        this.rarity = Rarity.RARE;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (level.isClient())
            return null;
        if (level instanceof IncursionLevel)
            return "inincursion";
        if (!level.getIdentifier().equals(LevelIdentifier.SURFACE_IDENTIFIER))
            return "notsurface";
        if(!level.getWorldEntity().isNight())
            return "notnight";
        return null;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (!level.isServer()) return item;

        if (!level.getWorldEntity().isNight()) {
            level.getServer().network.sendToAllClients(new PacketChatMessage("Raid can only be summoned at night."));
            return item;
        }

        ServerClient serverClient = player.getServerClient();
        ServerSettlementData settlementData = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), player.getTileX(), player.getTileY());
        if (settlementData == null) {
            player.getServerClient().sendChatMessage(new LocalMessage("raidersoncall", "nosettlement"));
            return item;
        }

        if (!settlementData.spawnRaid()) {
            String name = settlementData.networkData.getSettlementName().translate();
            player.getServerClient().sendChatMessage(new LocalMessage("raidersoncall", "notenoughsettlers"));
        }

        level.getServer().network.sendToAllClients(new PacketChatMessage("Finally a worthy opponent! Our battle will be legendary!"));
        item.setAmount(item.getAmount() - 1);
        return item;
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        if (level.isServer() && player != null && player.isServerClient() && error.equals("inincursion")) {
            player.getServerClient().sendChatMessage(new LocalMessage("raidersoncall", "cannotsummoninincursion"));
            return item;
        }

        if (level.isServer() && player != null && !error.equals("alreadyspawned"))
            player.getServerClient().sendChatMessage(new LocalMessage("raidersoncall", error));

        return item;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("tooltip", "summonraidtooltip1"));

        LocalMessage line2 = new LocalMessage("tooltip", "summonraidtooltip2");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < (tooltips.getWidth() / 6) - line2.translate().length(); i++) {
            builder.append(' ');
        }
        builder.append(line2.translate());
        tooltips.add(builder.toString());
        return tooltips;
    }
}
