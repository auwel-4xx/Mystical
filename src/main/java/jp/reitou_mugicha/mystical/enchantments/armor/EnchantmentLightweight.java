package jp.reitou_mugicha.mystical.enchantments.armor;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemType;

public class EnchantmentLightweight extends CustomEnchant
{
    @Override
    public Key getKey()
    {
        return Key.key("mystical:lightweight");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("ライトウェイト");
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }

    @Override
    public TagKey<ItemType> getSupportedItems()
    {
        return ItemTypeTagKeys.FOOT_ARMOR;
    }

    @Override
    public Component getTooltipDescription(int level)
    {
        return Component.text("耕地や亀の卵などの上を安全にジャンプできます。");
    }

    @Override
    public void onChangeBlock(LivingEntity entity, EntityChangeBlockEvent event, int level)
    {
        Block block = event.getBlock();
        Material original = block.getType();
        Material to = event.getTo();

        if (original == Material.FARMLAND && to == Material.DIRT)
        {
            event.setCancelled(true);
        }

        if (original == Material.BIG_DRIPLEAF && to == Material.BIG_DRIPLEAF && !entity.isSneaking())
        {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerInteract(Player player, PlayerInteractEvent event, int level)
    {
        if (event.getAction() != Action.PHYSICAL) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        if (block.getType() == Material.TURTLE_EGG)
        {
            event.setCancelled(true);
        }
    }
}
