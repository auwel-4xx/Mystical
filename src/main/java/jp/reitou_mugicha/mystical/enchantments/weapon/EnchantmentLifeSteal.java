package jp.reitou_mugicha.mystical.enchantments.weapon;

import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemType;

public class EnchantmentLifeSteal extends CustomEnchant
{
    @Override
    public Key getKey()
    {
        return Key.key("mystical:life_steal");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("ライフスチール");
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public TagKey<ItemType> getSupportedItems()
    {
        return ItemTagHelper.ATTACKS;
    }

    @Override
    public Component getTooltipDescription(int level)
    {
        return Component.text("攻撃した相手の体力を盗みます。");
    }

    @Override
    public void onHit(Player attacker, EntityDamageByEntityEvent event, int level)
    {
        double damage = event.getFinalDamage();
        double healAmount = damage * (level * 0.5);

        double newHealth = Math.min(
                attacker.getHealth() + healAmount,
                attacker.getAttribute(Attribute.MAX_HEALTH).getValue()
        );
        attacker.setHealth(newHealth);

        attacker.getWorld().spawnParticle(
                Particle.HEART,
                attacker.getLocation().add(0, 1, 0),
                (int) Math.ceil(healAmount),
                0.3, 0.3, 0.3, 0
        );
    }
}
