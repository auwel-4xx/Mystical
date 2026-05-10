package jp.reitou_mugicha.mystical.enchantments.shield;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.Vector;

public class EnchantmentRebound extends CustomEnchant
{
    private double getPower(int level)
    {
        return 2.0 + (level * 0.5);
    }

    @Override
    public Key getKey()
    {
        return Key.key("mystical:rebound");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("リバウンド");
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public TagKey<ItemType> getSupportedItems()
    {
        return ItemTagHelper.SHIELD;
    }

    @Override
    public Component getTooltipDescription(int level)
    {
        return Component.text("当たったエンティティを跳ね返します。");
    }

    @Override
    public void onShieldBlock(Player player, EntityDamageByEntityEvent event, int level)
    {
        Entity damager = event.getDamager();
        double power = getPower(level);

        if (damager instanceof LivingEntity attacker)
        {
            Vector vector = player.getLocation().getDirection().clone().multiply(power);
            vector.setY(0.5);
            attacker.setVelocity(vector);
        }
        else if (damager instanceof Projectile projectile)
        {
            projectile.remove();

            if (!(projectile.getShooter() instanceof LivingEntity shooter)) return;

            Vector direction = shooter.getLocation().toVector()
                    .subtract(player.getLocation().toVector())
                    .normalize()
                    .multiply(power);
            direction.setY(direction.getY() + 0.1);

            Arrow arrow = player.getWorld().spawnArrow(
                    player.getEyeLocation(),
                    direction,
                    (float) power,
                    1.0f
            );
            arrow.setShooter(player);
            arrow.setDamage(((Arrow) projectile).getDamage());
        }
    }
}
