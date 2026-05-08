package jp.reitou_mugicha.mystical.events;

import jp.reitou_mugicha.mystical.Helpers;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import jp.reitou_mugicha.mystical.core.EnchantBootstrap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilCompatibility implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getViewers().get(0) instanceof Player player)) return;
        if (!Helpers.isBedrock(player)) return;

        AnvilInventory inv = event.getInventory();
        ItemStack secondItem = inv.getItem(1);

        if (inv.getItem(0) != null && secondItem != null && secondItem.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) secondItem.getItemMeta();
            if (bookMeta == null) return;

            if (hasOnlyCustomEnchant(bookMeta)) {
                applyFakeUnbreaking(secondItem, bookMeta);
                inv.setItem(1, secondItem);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!Helpers.isBedrock(player)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        if (item.getType() == Material.ENCHANTED_BOOK) {
            removeFakeUnbreaking(item, player);
        }

        if (event.getInventory() instanceof AnvilInventory && event.getRawSlot() == 2) {
            removeFakeUnbreaking(item, player);
        }
    }

    private boolean hasOnlyCustomEnchant(EnchantmentStorageMeta meta) {
        boolean hasCustom = false;
        for (Enchantment stored : meta.getStoredEnchants().keySet()) {
            for (CustomEnchant custom : EnchantBootstrap.MANAGER.getEnchants()) {
                if (custom.getKey().equals(stored.getKey())) {
                    hasCustom = true;
                    break;
                }
            }
        }

        return hasCustom && meta.getStoredEnchants().size() == 1;
    }

    private void applyFakeUnbreaking(ItemStack item, EnchantmentStorageMeta meta) {
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addStoredEnchant(Enchantment.UNBREAKING, 1, false);
        item.setItemMeta(meta);
    }

    private void removeFakeUnbreaking(ItemStack item, Player player) {
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                storageMeta.removeStoredEnchant(Enchantment.UNBREAKING);
            } else {
                meta.removeEnchant(Enchantment.UNBREAKING);
            }

            meta.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
            item.setItemMeta(meta);

            player.updateInventory();
        }
    }
}