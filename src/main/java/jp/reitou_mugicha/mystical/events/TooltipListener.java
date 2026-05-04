package jp.reitou_mugicha.mystical.events;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import jp.reitou_mugicha.mystical.Mystical;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import jp.reitou_mugicha.mystical.core.EnchantManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TooltipListener implements PacketListener
{

    private final EnchantManager manager;
    private static final String DESCRIPTION_MARKER = "mystical:enchant_desc";

    public TooltipListener(EnchantManager manager) {
        this.manager = manager;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        PacketTypeCommon type = event.getPacketType();

        switch (type) {
            case PacketType.Play.Server.SET_SLOT -> {
                WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
                asBukkit(packet.getItem())
                        .map(this::addDescription)
                        .map(this::fromBukkit)
                        .ifPresent(packet::setItem);
            }
            case PacketType.Play.Server.WINDOW_ITEMS -> {
                WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
                packet.getItems().replaceAll(original ->
                        asBukkit(original).map(this::addDescription).map(this::fromBukkit).orElse(original)
                );
            }
            case PacketType.Play.Server.SET_PLAYER_INVENTORY -> {
                WrapperPlayServerSetPlayerInventory packet = new WrapperPlayServerSetPlayerInventory(event);
                asBukkit(packet.getStack())
                        .map(this::addDescription)
                        .map(this::fromBukkit)
                        .ifPresent(packet::setStack);
            }
            case PacketType.Play.Server.SET_CURSOR_ITEM -> {
                WrapperPlayServerSetCursorItem packet = new WrapperPlayServerSetCursorItem(event);
                asBukkit(packet.getStack())
                        .map(this::addDescription)
                        .map(this::fromBukkit)
                        .ifPresent(packet::setStack);
            }
            default -> { return; }
        }

        event.markForReEncode(true);
    }

    private Optional<org.bukkit.inventory.ItemStack> asBukkit(
            com.github.retrooper.packetevents.protocol.item.ItemStack peItem) {
        return Optional.ofNullable(peItem).map(SpigotConversionUtil::toBukkitItemStack);
    }

    private com.github.retrooper.packetevents.protocol.item.ItemStack fromBukkit(
            org.bukkit.inventory.ItemStack item) {
        return SpigotConversionUtil.fromBukkitItemStack(item);
    }

    private org.bukkit.inventory.ItemStack addDescription(org.bukkit.inventory.ItemStack item) {
        if (item == null || !item.hasItemMeta()) return item;

        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();

        lore.removeIf(this::isDescriptionLine);

        List<Component> descLines = new ArrayList<>();
        for (CustomEnchant custom : manager.getEnchants()) {
            int level = custom.getLevel(item);
            if (level == 0) continue;

            Component desc = custom.getTooltipDescription(level)
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .insertion(DESCRIPTION_MARKER);
            descLines.add(desc);
        }

        if (descLines.isEmpty()) return item;

        lore.addAll(descLines);

        org.bukkit.inventory.ItemStack clone = item.clone();
        org.bukkit.inventory.meta.ItemMeta cloneMeta = clone.getItemMeta();
        cloneMeta.lore(lore);
        clone.setItemMeta(cloneMeta);
        return clone;
    }

    private boolean isDescriptionLine(Component component) {
        return DESCRIPTION_MARKER.equals(component.insertion());
    }
}