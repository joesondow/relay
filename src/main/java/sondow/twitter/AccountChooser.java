package sondow.twitter;

import java.time.ZonedDateTime;
import java.util.List;

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

    public PromoterAndTarget choosePromoterAndTarget() {

        // Based on current day, choose promoter account name.
        ZonedDateTime now = time.now();
        List<String> accounts = botConfig.getAccounts();

        // Divide up the year into periods of x days where x is the number of accounts.
        int daysInPeriod = accounts.size();
        int dayOfYear = now.getDayOfYear();
        int indexOfPeriod = dayOfYear / daysInPeriod;
        int modIndex = (indexOfPeriod + dayOfYear) % daysInPeriod;
        String promoter = accounts.get(modIndex);
        int dayOfPeriod = dayOfYear % daysInPeriod;
        String target = accounts.get(dayOfPeriod);

        if (promoter.equals(target)) {
            // On days when calculated promoter and target are the same, pick a target
            // from the accounts that are not the promoter.
            accounts.remove(target);
            int dayOfShorterPeriod = dayOfYear % (daysInPeriod - 1);
            target = accounts.get(dayOfShorterPeriod);
        }
        return new PromoterAndTarget(promoter, target);
    }
}
