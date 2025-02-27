package sondow.social;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class AccountChooser {

    private final BotConfig botConfig;
    private final Time time;

    public AccountChooser(BotConfig botConfig) {
        this(botConfig, new Time());
    }

    public AccountChooser(BotConfig botConfig, Time time) {
        this.botConfig = botConfig;
        this.time = time;
    }

    public PromoterAndTarget chooseTwitterPromoterAndTarget() {

        // Based on current day, choose promoter account name.
        ZonedDateTime now = time.nowUtc();
        List<String> accounts = botConfig.getTwitterAccounts();

        // Divide up the year into periods of x days where x is the number of accounts.
        return choosePromoterAndTargetFrom(accounts, now);
    }

    public PromoterAndTarget chooseBlueskyPromoterAndTarget() {

        // Based on current day, choose promoter account name.
        ZonedDateTime now = time.nowUtc();
        LinkedHashMap<String, BlueskyConfig> blueskyShortHandlesToConfigs = botConfig.getBlueskyShortHandlesToConfigs();
        List<String> shortHandles = new ArrayList<>(blueskyShortHandlesToConfigs.keySet());

        return choosePromoterAndTargetFrom(shortHandles, now);
    }

    public PromoterAndTarget chooseMastodonPromoterAndTarget() {

        // Based on current day, choose promoter account name.
        ZonedDateTime now = time.nowUtc();
        LinkedHashMap<String, MastodonConfig> mastodonShortHandlesToConfigs = botConfig.getMastodonShortHandlesToConfigs();
        List<String> shortHandles = new ArrayList<>(mastodonShortHandlesToConfigs.keySet());

        return choosePromoterAndTargetFrom(shortHandles, now);
    }

    @NotNull private static PromoterAndTarget choosePromoterAndTargetFrom(List<String> shortHandles,
            ZonedDateTime now) {
        // Divide up the year into periods of x days where x is the number of accounts.
        int daysInPeriod = shortHandles.size();
        int dayOfYear = now.getDayOfYear();
        int indexOfPeriod = dayOfYear / daysInPeriod;
        int modIndex = (indexOfPeriod + dayOfYear) % daysInPeriod;
        String promoter = shortHandles.get(modIndex);
        int dayOfPeriod = dayOfYear % daysInPeriod;
        String target = shortHandles.get(dayOfPeriod);

        if (promoter.equals(target)) {
            // On days when calculated promoter and target are the same, pick a target
            // from the accounts that are not the promoter.
            shortHandles.remove(target);
            int dayOfShorterPeriod = dayOfYear % (daysInPeriod - 1);
            target = shortHandles.get(dayOfShorterPeriod);
        }
        return new PromoterAndTarget(promoter, target);
    }
}
