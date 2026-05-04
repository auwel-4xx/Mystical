package jp.reitou_mugicha.mystical.enchantments.universal;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.List;

public class EnchantmentFireProof extends CustomEnchant
{
    @Override
    public Key getKey()
    {
        return Key.key("mystical:fire_proof");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("延焼耐性");
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
        return Component.text("アイテムが火で消えなくなります。");
    }

    @Override
    public void onItemEntityDamaged(ItemStack itemStack, EntityDamageEvent event, int level)
    {
        if (event.getCause() == EntityDamageEvent.DamageCause.LAVA ||
            event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
            event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)
        {
            event.setCancelled(true);
        }
    }
}