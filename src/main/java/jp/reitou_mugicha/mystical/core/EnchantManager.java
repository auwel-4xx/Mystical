package jp.reitou_mugicha.mystical.core;

import jp.reitou_mugicha.mystical.enchantments.armor.EnchantmentFlameWalker;
import jp.reitou_mugicha.mystical.enchantments.armor.EnchantmentNightvision;
import jp.reitou_mugicha.mystical.enchantments.bow.EnchantmentSniper;
import jp.reitou_mugicha.mystical.enchantments.curse.EnchantmentUnstable;
import jp.reitou_mugicha.mystical.enchantments.fishing.EnchantmentLongThrow;
import jp.reitou_mugicha.mystical.enchantments.tool.EnchantmentTelepathy;
import jp.reitou_mugicha.mystical.enchantments.universal.EnchantmentFireProof;
import jp.reitou_mugicha.mystical.enchantments.universal.EnchantmentSoulbound;
import jp.reitou_mugicha.mystical.enchantments.weapon.EnchantmentLifeSteal;
import jp.reitou_mugicha.mystical.enchantments.armor.EnchantmentLightweight;
import jp.reitou_mugicha.mystical.enchantments.weapon.EnchantmentPoisonAspect;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantManager implements Listener
{
    private final List<CustomEnchant> enchants;

    public EnchantManager()
    {
        this.enchants = List.of(
            new EnchantmentTelepathy(),
            new EnchantmentPoisonAspect(),
            new EnchantmentFireProof(),
            new EnchantmentSoulbound(),
            new EnchantmentUnstable(),
            new EnchantmentFlameWalker(),
            new EnchantmentLifeSteal(),
            new EnchantmentLightweight(),
            new EnchantmentNightvision(),
            new EnchantmentSniper(),
            new EnchantmentLongThrow()
        );
    }

    public List<CustomEnchant> getEnchants()
    {
        return this.enchants;
    }

    public void registerListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void startTick(Plugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            List<CustomEnchant> tickEnchants = enchants.stream()
                    .filter(CustomEnchant::hasTick)
                    .toList();

            if (tickEnchants.isEmpty()) return;

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                List<ItemStack> items = new ArrayList<>();
                items.add(player.getInventory().getItemInMainHand());
                items.add(player.getInventory().getItemInOffHand());
                items.add(player.getInventory().getHelmet());
                items.add(player.getInventory().getChestplate());
                items.add(player.getInventory().getLeggings());
                items.add(player.getInventory().getBoots());

                for (ItemStack item : items) {
                    if (item == null || item.getType().isAir()) continue;
                    for (CustomEnchant enchant : tickEnchants) {
                        int level = enchant.getLevel(item);
                        if (level > 0) enchant.onTick(player, item, level);
                    }
                }
            }
        }, 0L, 1L);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        ItemStack item = attacker.getInventory().getItemInMainHand();

        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(item);
            if (level > 0) enchant.onHit(attacker, event, level);
        }
    }

    @EventHandler
    public void onDamagedByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        List<ItemStack> armors = List.of(
                victim.getInventory().getHelmet(),
                victim.getInventory().getChestplate(),
                victim.getInventory().getLeggings(),
                victim.getInventory().getBoots()
        );

        for (ItemStack armor : armors) {
            if (armor == null || armor.getType().isAir()) continue;
            for (CustomEnchant enchant : enchants) {
                int level = enchant.getLevel(armor);
                if (level > 0) enchant.onDamagedByEntity(victim, event, level);
            }
        }
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player victim)) return;

        List<ItemStack> armors = List.of(
                victim.getInventory().getHelmet(),
                victim.getInventory().getChestplate(),
                victim.getInventory().getLeggings(),
                victim.getInventory().getBoots()
        );

        for (ItemStack armor : armors) {
            if (armor == null || armor.getType().isAir()) continue;
            for (CustomEnchant enchant : enchants) {
                int level = enchant.getLevel(armor);
                if (level > 0) enchant.onDamaged(victim, event, level);
            }
        }
    }

    @EventHandler
    public void onItemEntityDamaged(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Item item)) return;
        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(item.getItemStack());
            if (level > 0) enchant.onItemEntityDamaged(item.getItemStack(), event, level);
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        ItemStack item = killer.getInventory().getItemInMainHand();

        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(item);
            if (level > 0) enchant.onKill(killer, event, level);
        }
    }

    @EventHandler()
    public void onDeath(PlayerDeathEvent event)
    {
        Player player = event.getPlayer();

        List<ItemStack> allItems = new java.util.ArrayList<>();
        allItems.add(player.getInventory().getItemInMainHand());
        allItems.add(player.getInventory().getItemInOffHand());
        allItems.add(player.getInventory().getHelmet());
        allItems.add(player.getInventory().getChestplate());
        allItems.add(player.getInventory().getLeggings());
        allItems.add(player.getInventory().getBoots());
        allItems.addAll(Arrays.asList(player.getInventory().getContents()));

        for (ItemStack item : allItems) {
            if (item == null || item.getType().isAir()) continue;
            for (CustomEnchant enchant : enchants) {
                int level = enchant.getLevel(item);
                if (level > 0) enchant.onDeath(player, item, event, level);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack boots = player.getInventory().getBoots();

        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(boots);
            if (level > 0) enchant.onMove(player, event, level);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(mainHand);
            if (level > 0) enchant.onBlockBreak(player, event, level);
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event)
    {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(mainHand);
            if (level > 0) enchant.onBlockDropItem(player, event, level);
        }
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event)
    {
        Player player = event.getPlayer();
        ItemStack damagedItem = event.getItem();

        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(damagedItem);
            if (level > 0) enchant.onItemDamaged(player, event, level);
        }
    }

    @EventHandler
    public void onChangeBlock(EntityChangeBlockEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.getEquipment() == null) return;

        ItemStack foot = entity.getEquipment().getBoots();
        if (foot == null || foot.getType().isAir()) return;

        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(foot);
            if (level > 0) enchant.onChangeBlock(entity, event, level);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(mainHand);
            if (level > 0) enchant.onPlayerInteract(player, event, level);
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event)
    {
        LivingEntity entity = event.getEntity();
        ItemStack itemStack = event.getBow();
        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(itemStack);
            if (level > 0) enchant.onShoot(entity, event, level);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event)
    {
        Player player = event.getPlayer();
        ItemStack fishingRod = player.getInventory().getItemInMainHand();
        for (CustomEnchant enchant : enchants) {
            int level = enchant.getLevel(fishingRod);
            if (level > 0) enchant.onFish(player, event, level);
        }
    }
}