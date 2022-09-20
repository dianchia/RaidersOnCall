package RaidersOnCall.items.consumables;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SummonRaidScrollItem extends ConsumableItem {
    public SummonRaidScrollItem() {
        super(1, true);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
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
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        if (!level.isServerLevel()) return item;

        if (!level.getWorldEntity().isNight()) {
            level.getServer().network.sendToAllClients(new PacketChatMessage("Raid can only be summoned at night."));
            return item;
        }

        try{
            SettlementLevelData levelData = SettlementLevelData.getSettlementData(level);
            level.getServer().network.sendToAllClients(new PacketChatMessage("Finally a worthy opponent! Our battle will be legendary!"));
            levelData.spawnRaid();
            item.setAmount(item.getAmount() - 1);
        } catch (NullPointerException ex){
            level.getServer().network.sendToAllClients(new PacketChatMessage("Unable to spawn raid!"));
            level.getServer().network.sendToAllClients(new PacketChatMessage("Either settlement is not found or number of settlers is lesser than 3."));
        }
        return item;
    }
}
