package jp.reitou_mugicha.mystical.enchantments.weapon;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import jp.reitou_mugicha.mystical.core.CustomEnchant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class EnchantmentPoisonAspect extends CustomEnchant
{
    @Override
    public Key getKey()
    {
        return Key.key("mystical:poison_aspect");
    }

    @Override
    public Component getDescription()
    {
        return Component.text("毒属性");
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
        return Component.text("攻撃した敵に" + String.format("%.2f", 40 + (float)level * 20) + "秒間の毒を与えます。");
    }

    @Override
    public void onHit(Player attacker, EntityDamageByEntityEvent event, int level)
    {
        if (getLevel(attacker.getInventory().getItemInMainHand()) <= 0) return;

        LivingEntity victim = (LivingEntity)event.getEntity();
        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40 + level * 20, level - 1));
    }
}
