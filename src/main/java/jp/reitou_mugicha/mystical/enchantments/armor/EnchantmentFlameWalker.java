package jp.reitou_mugicha.mystical.enchantments.armor;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.Mystical;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Levelled;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class EnchantmentFlameWalker extends CustomEnchant
{
    private static final BlockFace[] BLOCK_FACES = {BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};

    private int getRadius(int level) {
        return level * 2;
    }

    @Override
    public Key getKey() {
        return Key.key("mystical:flame_walker");
    }

    @Override
    public Component getDescription() {
        return Component.text("溶岩渡り");
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public TagKey<ItemType> getSupportedItems() {
        return ItemTypeTagKeys.FOOT_ARMOR;
    }

    @Override
    public Component getTooltipDescription(int level) {
        return Component.text("溶岩を固めて上を歩けるようにします。");
    }

    @Override
    public RegistryKeySet<Enchantment> getExclusiveWith() {
        return RegistrySet.keySet(RegistryKey.ENCHANTMENT, EnchantmentKeys.FROST_WALKER);
    }

    @Override
    public void onMove(Player player, PlayerMoveEvent event, int level) {
        Location to = event.getTo();

        Block belowBlock = to.getBlock().getRelative(BlockFace.DOWN);
        boolean nearLava = Stream.of(BLOCK_FACES)
                .anyMatch(face -> belowBlock.getRelative(face).getType() == Material.LAVA);
        if (!nearLava) return;

        int radius = getRadius(level);
        Set<Block> converted = replaceLava(player, radius);
        if (converted.isEmpty()) return;

        long delayTicks = (3L + level) * 20L;

        Bukkit.getScheduler().runTaskLater(
                Mystical.getInstance(),
                () -> converted.forEach(block -> {
                    if (block.getType() != Material.MAGMA_BLOCK) return;
                    block.setType(Material.LAVA);
                }),
                delayTicks
        );
    }

    @Override
    public void onDamaged(Player victim, EntityDamageEvent event, int level)
    {
        if (event.getCause() != EntityDamageEvent.DamageCause.HOT_FLOOR) return;
        event.setCancelled(true);
    }

    private Set<Block> replaceLava(Player player, int radius) {
        Set<Block> converted = new HashSet<>();

        for (Block block : getCircleBlocks(player, radius)) {
            if (block.getType() != Material.LAVA) continue;

            Block above = block.getRelative(BlockFace.UP);
            if (!above.isEmpty()) continue;

            BlockState state = Material.MAGMA_BLOCK.createBlockData().createBlockState();

            BlockFormEvent formEvent = new EntityBlockFormEvent(player, block, state);
            Bukkit.getPluginManager().callEvent(formEvent);
            if (formEvent.isCancelled()) continue;

            block.setBlockData(state.getBlockData());
            converted.add(block);
        }

        return converted;
    }

    private List<Block> getCircleBlocks(Player player, int radius) {
        World world = player.getWorld();
        Location loc = player.getLocation();
        int centerX = loc.getBlockX();
        int centerZ = loc.getBlockZ();
        int fixedY  = loc.getBlockY() - 1;

        List<Block> blocks = new ArrayList<>();
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                int dx = centerX - x;
                int dz = centerZ - z;
                if (dx * dx + dz * dz <= radius * radius) {
                    blocks.add(world.getBlockAt(x, fixedY, z));
                }
            }
        }
        return blocks;
    }
}