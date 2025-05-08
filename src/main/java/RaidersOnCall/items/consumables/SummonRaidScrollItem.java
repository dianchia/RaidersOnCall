package RaidersOnCall.items.consumables;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementRaidOptions;

public class SummonRaidScrollItem extends ConsumableItem {
    public SummonRaidScrollItem() {
        super(1, true);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(new LocalMessage("tooltip", "summonraidtooltip1"));

        LocalMessage line2 = new LocalMessage("tooltip", "summonraidtooltip2");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < (tooltips.getWidth() / 6) - line2.translate().length(); i++) {
            builder.append(' ');
        }
        builder.append(line2.translate());
        tooltips.add(builder.toString());
        return tooltips;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (!level.isServer()) return item;

        if (!level.getWorldEntity().isNight()) {
            level.getServer().network.sendToAllClients(new PacketChatMessage("Raid can only be summoned at night."));
            return item;
        }

        SettlementLevelData settlementData = SettlementLevelData.getSettlementData(level);
        if (settlementData == null) {
            level.getServer().network.sendToAllClients(new PacketChatMessage("Settlement is not found."));
            return item;
        }

        if (settlementData.countTotalSettlers() < 3) {
            level.getServer().network.sendToAllClients(new PacketChatMessage("Come find me when you have more than 3 settlers."));
            return item;
        }

        level.getServer().network.sendToAllClients(new PacketChatMessage("Finally a worthy opponent! Our battle will be legendary!"));
        item.setAmount(item.getAmount() - 1);

        SettlementRaidOptions options = settlementData.getRaidOptions(false);
        options.difficultyModifier = settlementData.getNextRaidDifficultyMod();
        options.direction = GameRandom.globalRandom.getOneOf(SettlementRaidLevelEvent.RaidDir.values());
        options.dontAutoAttackSettlement = false;
        settlementData.spawnRaid(settlementData.getNextRaid(options), options);

        return item;
    }
}
