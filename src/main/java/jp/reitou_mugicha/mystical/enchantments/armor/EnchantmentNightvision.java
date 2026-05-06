package jp.reitou_mugicha.mystical.enchantments.armor;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EnchantmentNightvision extends CustomEnchant
{
    @Override
    public Key getKey()
    {
        return Key.key("mystical:night_vision");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("暗視");
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }

    @Override
    public TagKey<ItemType> getSupportedItems()
    {
        return ItemTypeTagKeys.HEAD_ARMOR;
    }

    @Override
    public Component getTooltipDescription(int level)
    {
        return Component.text("暗いところでも安心！");
    }

    @Override
    public boolean hasTick()
    {
        return true;
    }

    @Override
    public void onTick(Player player, ItemStack item, int level)
    {
        PotionEffect current = player.getPotionEffect(PotionEffectType.NIGHT_VISION);
        if (current != null && current.getDuration() > 260) return;

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                300,
                0,
                true,
                false
        ));
    }
}
