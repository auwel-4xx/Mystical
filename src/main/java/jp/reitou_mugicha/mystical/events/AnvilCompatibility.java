package jp.reitou_mugicha.mystical.events;

import jp.reitou_mugicha.mystical.Helpers;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import jp.reitou_mugicha.mystical.core.EnchantBootstrap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnvilCompatibility implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getViewers().get(0) instanceof Player player)) return;
        if (!Helpers.isBedrock(player)) return;

        AnvilInventory inv = event.getInventory();
        ItemStack left  = inv.getItem(0);
        ItemStack right = inv.getItem(1);

        if (left == null || right == null) return;
        if (right.getType() != Material.ENCHANTED_BOOK) return;
        if (!(right.getItemMeta() instanceof EnchantmentStorageMeta bookMeta)) return;

        List<CustomEnchant> matchedCustoms = getMatchedCustomEnchants(bookMeta);
        if (matchedCustoms.isEmpty()) return;
        if (!bookMeta.hasStoredEnchant(Enchantment.VANISHING_CURSE)) {
            bookMeta.addStoredEnchant(Enchantment.VANISHING_CURSE, 1, true);
            right.setItemMeta(bookMeta);
            inv.setItem(1, right);
        }

        ItemStack result = buildResult(left, bookMeta, matchedCustoms);
        if (result != null) {
            event.setResult(result);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!Helpers.isBedrock(player)) return;
        if (!(event.getInventory() instanceof AnvilInventory anvilInv)) return;
        if (event.getRawSlot() != 2) return;

        ItemStack result = event.getCurrentItem();
        if (result == null || result.getType() == Material.AIR) return;

        removeVanishingCurse(result);

        ItemStack right = anvilInv.getItem(1);
        if (right != null && right.getType() == Material.ENCHANTED_BOOK
                && right.getItemMeta() instanceof EnchantmentStorageMeta bookMeta) {
            if (bookMeta.hasStoredEnchant(Enchantment.VANISHING_CURSE)) {
                bookMeta.removeStoredEnchant(Enchantment.VANISHING_CURSE);
                right.setItemMeta(bookMeta);
                anvilInv.setItem(1, right);
            }
        }

        event.setCurrentItem(result);
    }

    private List<CustomEnchant> getMatchedCustomEnchants(EnchantmentStorageMeta bookMeta) {
        List<CustomEnchant> matched = new ArrayList<>();
        for (Enchantment enc : bookMeta.getStoredEnchants().keySet()) {
            for (CustomEnchant custom : EnchantBootstrap.MANAGER.getEnchants()) {
                if (custom.getKey().equals(enc.getKey())) {
                    matched.add(custom);
                    break;
                }
            }
        }
        return matched;
    }

    private ItemStack buildResult(ItemStack base,
                                  EnchantmentStorageMeta bookMeta,
                                  List<CustomEnchant> customEnchants) {
        ItemStack result = base.clone();
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return null;

        for (Map.Entry<Enchantment, Integer> entry : bookMeta.getStoredEnchants().entrySet()) {
            Enchantment enc = entry.getKey();
            int level       = entry.getValue();

            if (enc.equals(Enchantment.VANISHING_CURSE)) continue;

            meta.addEnchant(enc, level, true);
        }

        result.setItemMeta(meta);
        return result;
    }

    private void removeVanishingCurse(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            if (storageMeta.hasStoredEnchant(Enchantment.VANISHING_CURSE)) {
                storageMeta.removeStoredEnchant(Enchantment.VANISHING_CURSE);
                item.setItemMeta(storageMeta);
            }
        } else {
            if (meta.hasEnchant(Enchantment.VANISHING_CURSE)) {
                meta.removeEnchant(Enchantment.VANISHING_CURSE);
                item.setItemMeta(meta);
            }
        }
    }
}