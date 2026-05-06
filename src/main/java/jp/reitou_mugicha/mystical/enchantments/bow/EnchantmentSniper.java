package jp.reitou_mugicha.mystical.enchantments.bow;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemType;

public class EnchantmentSniper extends CustomEnchant
{
    public double getPower(int level)
    {
        return 1.3 + (level * 0.5);
    }

    @Override
    public Key getKey()
    {
        return Key.key("mystical:sniper");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("スナイパー");
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    public TagKey<ItemType> getSupportedItems()
    {
        return ItemTypeTagKeys.ENCHANTABLE_BOW;
    }

    @Override
    public Component getTooltipDescription(int level)
    {
        return Component.text("発射する矢の速度が上昇します。");
    }

    @Override
    public void onShoot(LivingEntity entity, EntityShootBowEvent event, int level)
    {
        double power = getPower(level);
        Entity arrow = event.getProjectile();
        arrow.setVelocity(arrow.getVelocity().multiply(power));
    }
}
