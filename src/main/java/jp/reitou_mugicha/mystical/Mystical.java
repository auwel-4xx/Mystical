package jp.reitou_mugicha.mystical;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import jp.reitou_mugicha.mystical.core.EnchantBootstrap;
import jp.reitou_mugicha.mystical.events.AnvilCompatibility;
import jp.reitou_mugicha.mystical.events.TooltipListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mystical extends JavaPlugin
{
    public static Mystical Instance;

    public Mystical()
    {
        Instance = this;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable()
    {
        getLogger().info(ChatColor.LIGHT_PURPLE + "Mystical has been enabled.");

        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(
                new TooltipListener(EnchantBootstrap.MANAGER),
                PacketListenerPriority.NORMAL
        );

        getServer().getPluginManager().registerEvents(new AnvilCompatibility(), this);

        EnchantBootstrap.MANAGER.registerListener(this);
        EnchantBootstrap.MANAGER.startTick(this);
    }

    @Override
    public void onDisable()
    {
        getLogger().info(ChatColor.LIGHT_PURPLE + "Mystical has been disabled.");
        PacketEvents.getAPI().terminate();
    }

    public static Plugin getInstance()
    {
        return Instance;
    }
}
