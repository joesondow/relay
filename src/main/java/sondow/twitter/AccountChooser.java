package sondow.twitter;

import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
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

    public String chooseTarget() {
        // Based on current day, choose target account name.
        ZonedDateTime now = time.now();
        List<String> accounts = botConfig.getAccounts();
        int daysInPeriod = accounts.size();

        // Divide up the year into periods of x days where x is the number of accounts.
//        int year = now.getYear();
        int dayOfYear = now.getDayOfYear();

//        boolean isLeapYear = GregorianCalendar.from(now).isLeapYear(year);
//        int daysInYear = isLeapYear ? 366 : 365;

        // What day of the period is itPeriod = dayOfYear % daysInPeriod; // Zero indexed
        int dayOfPeriod = dayOfYear % daysInPeriod;
        return accounts.get(dayOfPeriod);
    }

    public String choosePromoter() {
        // Based on current day, choose promoter account name.



        return "EmojiAquarium";
    }
}
