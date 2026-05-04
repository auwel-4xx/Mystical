package jp.reitou_mugicha.mystical;

import java.util.concurrent.ThreadLocalRandom;

public class Helpers
{
    public static boolean probability(double percent)
    {
        if (percent < 0 || percent >= 100)
        {
            throw new IllegalArgumentException("percent must be >= 0 and < 100");
        }
        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < percent;
    }
}