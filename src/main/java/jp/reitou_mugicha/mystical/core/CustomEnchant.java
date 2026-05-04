package jp.reitou_mugicha.mystical.core;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.List;

public abstract class CustomEnchant
{
    public abstract Key getKey();
    public abstract Component getDescription();
    public abstract int getMaxLevel();
    public abstract TagKey<ItemType> getSupportedItems();
    public abstract Component getTooltipDescription(int level);

    public TagKey<ItemType> getPrimaryItems() { return null; }

    public int getWeight() { return 5; }
    public int getAnvilCost() { return 1; }

    public EnchantmentRegistryEntry.EnchantmentCost getMinCost()
    {
        return EnchantmentRegistryEntry.EnchantmentCost.of(1, 1);
    }

    public EnchantmentRegistryEntry.EnchantmentCost getMaxCost()
    {
        return EnchantmentRegistryEntry.EnchantmentCost.of(50, 5);
    }

    public void onTick(Player player, ItemStack itemStack, int level) {}
    public void onHit(Player attacker, EntityDamageByEntityEvent event, int level) {}
    public void onDamaged(Player victim, EntityDamageByEntityEvent event, int level) {}

    public void onEntityDamaged(ItemStack itemStack, EntityDamageEvent event, int level) {}
    public void onKill(Player player, EntityDeathEvent event, int level) {}
    public void onDeath(Player player, ItemStack itemStack, PlayerDeathEvent event, int level) {}
    public void onMove(Player player, PlayerMoveEvent event, int level) {}
    public void onBlockBreak(Player player, BlockBreakEvent event, int level) {}
    public void onBlockDropItem(Player player, BlockDropItemEvent event, int level) {}
    public void onItemDamaged(Player player, PlayerItemDamageEvent event, int level) {}

    public boolean hasTick() { return false; }
    public boolean isCurse() { return false; }
    public RegistryKeySet<Enchantment> getExclusiveWith() { return RegistrySet.keySet(RegistryKey.ENCHANTMENT); }

    public Enchantment getBukkitEnchantment()
    {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(EnchantmentKeys.create(getKey()));
    }

    public int getLevel(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.getType().isAir()) return 0;
        Enchantment enchantment = getBukkitEnchantment();
        if (enchantment == null) return 0;
        return itemStack.getEnchantmentLevel(enchantment);
    }
}