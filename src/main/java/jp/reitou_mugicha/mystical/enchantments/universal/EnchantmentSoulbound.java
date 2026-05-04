package jp.reitou_mugicha.mystical.enchantments.universal;

import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import jp.reitou_mugicha.mystical.core.EnchantBootstrap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentSoulbound extends CustomEnchant
{
    @Override
    public Key getKey()
    {
        return Key.key("mystical:soulbound");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("ソウルバウンド");
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }

    @Override
    public TagKey<ItemType> getSupportedItems()
    {
        return ItemTagHelper.ALL;
    }

    @Override
    public Component getTooltipDescription(int level)
    {
        return Component.text("死んでもアイテムが引き継がれます。");
    }

    @Override
    public void onDeath(Player player, ItemStack itemStack, PlayerDeathEvent event, int level)
    {
        List<ItemStack> drops = event.getDrops();
        List<ItemStack> itemsToKeep = new ArrayList<>();

        drops.removeIf(item -> {
            for(CustomEnchant enchant : EnchantBootstrap.MANAGER.getEnchants())
            {
                int enchantLevel = enchant.getLevel(item);
                if (enchantLevel > 0 && enchant.getKey() == new EnchantmentSoulbound().getKey())
                {
                    itemsToKeep.add(item);
                    return true;
                }
            }
            return false;
        });

        if (!itemsToKeep.isEmpty())
        {
            event.getItemsToKeep().addAll(itemsToKeep);
        }
    }
}
