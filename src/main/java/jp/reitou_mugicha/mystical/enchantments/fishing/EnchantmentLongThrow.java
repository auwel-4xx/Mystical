package jp.reitou_mugicha.mystical.enchantments.fishing;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemType;

public class EnchantmentLongThrow extends CustomEnchant
{
    public double getPower(int level)
    {
        return 1.7 * (level * 0.5);
    }

    @Override
    public Key getKey()
    {
        return Key.key("mystical:long_throw");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("ロングスロー");
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    public TagKey<ItemType> getSupportedItems()
    {
        return ItemTypeTagKeys.ENCHANTABLE_FISHING;
    }

    @Override
    public Component getTooltipDescription(int level)
    {
        return Component.text("釣り針を遠くに投げられます。");
    }

    @Override
    public void onFish(Player player, PlayerFishEvent event, int level)
    {
        if (event.getState() != PlayerFishEvent.State.FISHING) return;

        FishHook hook = event.getHook();
        hook.setVelocity(hook.getVelocity().multiply(getPower(level)));
    }
}
