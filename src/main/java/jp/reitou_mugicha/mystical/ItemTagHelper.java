package jp.reitou_mugicha.mystical;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;

public class ItemTagHelper
{
    public static final TagKey<ItemType> TOOLS = TagKey.create(RegistryKey.ITEM, Key.key("mystical", "tools"));
    public static final TagKey<ItemType> ARMORS = TagKey.create(RegistryKey.ITEM, Key.key("mystical", "armors"));
    public static final TagKey<ItemType> ALL = TagKey.create(RegistryKey.ITEM, Key.key("mystical", "all"));
}