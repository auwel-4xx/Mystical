package jp.reitou_mugicha.mystical.core;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import jp.reitou_mugicha.mystical.ItemTagHelper;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EnchantBootstrap implements PluginBootstrap {

    public static final EnchantManager MANAGER = new EnchantManager();

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        var lifecycle = context.getLifecycleManager();

        // Register Tags
        lifecycle.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM).newHandler(event -> {
            var registrar = event.registrar();

            registrar.addToTag(ItemTagHelper.TOOLS, List.of(
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "pickaxes"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "shovels"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "axes"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "hoes")))
            ));

            registrar.addToTag(ItemTagHelper.ARMORS, List.of(
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "head_armor"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "chest_armor"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "leg_armor"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "foot_armor")))
            ));

            registrar.addToTag(ItemTagHelper.ALL, List.of(
                    TagEntry.tagEntry(ItemTagHelper.TOOLS),
                    TagEntry.tagEntry(ItemTagHelper.ARMORS),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "swords"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "enchantable/bow"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "enchantable/crossbow"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "enchantable/trident"))),
                    TagEntry.valueEntry(TypedKey.create(RegistryKey.ITEM, Key.key("minecraft", "elytra")))
            ));

            registrar.addToTag(ItemTagHelper.ATTACKS, List.of(
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "swords"))),
                    TagEntry.tagEntry(TagKey.create(RegistryKey.ITEM, Key.key("minecraft", "axes")))
            ));

            registrar.addToTag(ItemTagHelper.SHIELD, List.of(
                    TagEntry.valueEntry(TypedKey.create(RegistryKey.ITEM, Key.key("minecraft", "shield")))
            ));
        }));

        // Register Enchants
        lifecycle.registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            for (CustomEnchant enchant : MANAGER.getEnchants()) {
                event.registry().register(
                        EnchantmentKeys.create(enchant.getKey()),
                        b -> b
                                .description(enchant.getDescription())
                                .maxLevel(enchant.getMaxLevel())
                                .weight(enchant.getWeight())
                                .anvilCost(enchant.getAnvilCost())
                                .minimumCost(enchant.getMinCost())
                                .maximumCost(enchant.getMaxCost())
                                .activeSlots(EquipmentSlotGroup.HAND)
                                .exclusiveWith(enchant.getExclusiveWith())
                                .supportedItems(event.getOrCreateTag(enchant.getSupportedItems()))
                );
            }
        }));

        // Register Tags
        lifecycle.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ENCHANTMENT).newHandler(event -> {
            var registrar = event.registrar();

            for (CustomEnchant enchant : MANAGER.getEnchants()) {
                var entry = Set.of(TagEntry.valueEntry(EnchantmentKeys.create(enchant.getKey())));

                if (enchant.isCurse()) {
                    registrar.addToTag(EnchantmentTagKeys.CURSE, entry);
                } else if (!enchant.isTreasure()) {
                    registrar.addToTag(EnchantmentTagKeys.IN_ENCHANTING_TABLE, entry);
                }

                if (enchant.isTradeable()) {
                    registrar.addToTag(EnchantmentTagKeys.TRADEABLE, entry);
                    registrar.addToTag(EnchantmentTagKeys.DOUBLE_TRADE_PRICE, entry); // 呪いなら価格2倍
                }

                if (enchant.isOnRandomLoot()) {
                    registrar.addToTag(EnchantmentTagKeys.ON_RANDOM_LOOT, entry);
                }
            }
        }));
    }
}