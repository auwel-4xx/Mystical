package jp.reitou_mugicha.mystical.events;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import jp.reitou_mugicha.mystical.Mystical;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import jp.reitou_mugicha.mystical.core.EnchantManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TooltipListener extends PacketListenerAbstract {

    private final EnchantManager manager;

    public TooltipListener(EnchantManager manager) {
        this.manager = manager;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
            ItemStack item = packet.getItem();

            if (injectTooltip(item)) {
                packet.setItem(item);
                event.markForReEncode(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
            boolean changed = false;
            List<ItemStack> items = packet.getItems();
            for (ItemStack item : items) {
                if (injectTooltip(item)) changed = true;
            }
            if (changed) {
                packet.setItems(items);
                event.markForReEncode(true);
            }
        }
    }

    private boolean injectTooltip(ItemStack peItem) {
        if (peItem == null || peItem.isEmpty()) return false;

        org.bukkit.inventory.ItemStack bukkitItem = SpigotConversionUtil.toBukkitItemStack(peItem);
        if (bukkitItem == null || !bukkitItem.hasItemMeta()) return false;

        List<Component> descLines = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> entry : bukkitItem.getEnchantments().entrySet()) {
            Enchantment enc = entry.getKey();
            int level = entry.getValue();

            for (CustomEnchant custom : manager.getEnchants()) {
                if (!enc.getKey().equals(custom.getKey())) continue;
                Component desc = custom.getTooltipDescription(level);
                if (!Component.empty().equals(desc)) {
                    descLines.add(desc.color(NamedTextColor.GRAY));
                }
            }
        }

        if (descLines.isEmpty()) return false;

        org.bukkit.inventory.meta.ItemMeta meta = bukkitItem.getItemMeta();
        List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
        lore.addAll(0, descLines);
        meta.lore(lore);
        bukkitItem.setItemMeta(meta);

        ItemStack converted = SpigotConversionUtil.fromBukkitItemStack(bukkitItem);
        peItem.setNBT(converted.getNBT());
        peItem.setAmount(converted.getAmount());

        return true;
    }
}