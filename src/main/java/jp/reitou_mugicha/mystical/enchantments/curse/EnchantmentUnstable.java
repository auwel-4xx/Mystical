package jp.reitou_mugicha.mystical.enchantments.curse;

import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.Helpers;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.List;

public class EnchantmentUnstable extends CustomEnchant
{
    @Override
    public Key getKey()
    {
        return Key.key("mystical:unstable");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("不安定");
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
        return Component.text("まれにツールが壊れます！");
    }

    @Override
    public void onItemDamaged(Player player, PlayerItemDamageEvent event, int level)
    {
        if (Helpers.probability(20))
        {
            ItemStack itemStack = event.getItem();
            itemStack.setAmount(0);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 1);
        }
    }
}
