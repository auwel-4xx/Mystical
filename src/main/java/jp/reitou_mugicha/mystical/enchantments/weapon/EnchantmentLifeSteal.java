package jp.reitou_mugicha.mystical.enchantments.weapon;

import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EnchantmentLifeSteal extends CustomEnchant
{
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 3000;

    private static final Set<EntityType> EXCLUDED_ENTITIES = Set.of(
            EntityType.ARMOR_STAND,
            EntityType.BAT,
            EntityType.COD,
            EntityType.SALMON,
            EntityType.TROPICAL_FISH,
            EntityType.PUFFERFISH,
            EntityType.TADPOLE,
            EntityType.SQUID,
            EntityType.GLOW_SQUID
    );

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
        if (!(event.getEntity() instanceof Player) && EXCLUDED_ENTITIES.contains(event.getEntityType())) return;

        UUID uuid = attacker.getUniqueId();
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(uuid) && now - cooldowns.get(uuid) < COOLDOWN_MS) return;

        cooldowns.put(uuid, now);

        double damage = event.getFinalDamage();
        double healAmount = damage * (level * 0.2);

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