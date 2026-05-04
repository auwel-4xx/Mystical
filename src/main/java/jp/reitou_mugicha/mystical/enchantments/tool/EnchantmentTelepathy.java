package jp.reitou_mugicha.mystical.enchantments.tool;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentTelepathy extends CustomEnchant
{
    @Override
    public Key getKey()
    {
        return Key.key("mystical:telepathy");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("テレパシー");
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }

    @Override
    public TagKey<ItemType> getSupportedItems()
    {
        return ItemTagHelper.TOOLS;
    }

    @Override
    public Component getTooltipDescription(int level)
    {
        return Component.text("採掘したブロックをインベントリに直接入れます。");
    }


    @Override
    public void onBlockDropItem(Player player, BlockDropItemEvent event, int level)
    {
        Inventory inventory = player.getInventory();

        event.setCancelled(true);

        var drops = event.getItems();
        List<Item> overflowedItems = new ArrayList<>();

        for (var drop : drops)
        {
            if (inventory.firstEmpty() == -1)
            {
                overflowedItems.add(drop);
                continue;
            }

            inventory.addItem(drop.getItemStack());
        }

        for (var item : overflowedItems)
        {
            player.getWorld().dropItemNaturally(event.getBlock().getLocation(), item.getItemStack());
        }
    }
}