package jp.reitou_mugicha.mystical.enchantments.universal;

import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemType;

public class EnchantmentExperience extends CustomEnchant
{
    private double getAmount(int level)
    {
        return 1.0 + (level * 0.5);
    }

    @Override
    public Key getKey()
    {
        return Key.key("mystical:experience");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("経験値増加");
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public TagKey<ItemType> getSupportedItems()
    {
        return ItemTagHelper.ATTACKS_AND_TOOLS;
    }

    @Override
    public Component getTooltipDescription(int level)
    {
        return Component.text("経験値量を増加します。");
    }

    @Override
    public void onKill(Player player, EntityDeathEvent event, int level)
    {
        int droppedExp = event.getDroppedExp();
        if (droppedExp > 0) {
            double multiplier = getAmount(level);
            int newExp = (int) (droppedExp * multiplier);

            event.setDroppedExp(newExp);
        }
    }

    @Override
    public void onBlockBreak(Player player, BlockBreakEvent event, int level)
    {
        int droppedExp = event.getExpToDrop();
        if (droppedExp > 0) {
            double multiplier = getAmount(level);
            int newExp = (int) (droppedExp * multiplier);

            event.setExpToDrop(newExp);
        }
    }
}
