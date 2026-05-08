package jp.reitou_mugicha.mystical;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.concurrent.ThreadLocalRandom;

public class Helpers
{
    public static boolean isBedrock(Player player)
    {
        return player.getUniqueId().getMostSignificantBits() == 0;
    }

    public static boolean probability(double percent)
    {
        if (percent < 0 || percent >= 100)
        {
            throw new IllegalArgumentException("percent must be >= 0 and < 100");
        }
        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < percent;
    }
}