package sondow.twitter;

import twitter4j.Status;
import twitter4j.conf.Configuration;

/**
 * The main application logic.
 */
public class Bot {

    private final BotConfig botConfig;

    Bot() {
        this.botConfig = new BotConfigFactory().configure();
    }

    /**
     * Performs the application logic.
     *
     * @return the posted tweet
     */
    public Status go() {

        // Find this account's retweets dating back two weeks or 60 tweets.
        AccountChooser accountChooser = new AccountChooser(botConfig);
        PromoterAndTarget promoterAndTarget = accountChooser.choosePromoterAndTarget();
        String targetScreenName = promoterAndTarget.getTarget();
        Configuration targetConfig = botConfig.getConfig(targetScreenName);

        Retweeter targetRetweeter = new Retweeter(targetConfig);

        Criteria criteria;
        if (botConfig.isPollAccount(targetScreenName)) {
            criteria = Criteria.POLL;
//        } else if (botConfig.isPinnedAccount(targetScreenName)) {
//            criteria = Criteria.PINNED;
        } else {
            criteria = Criteria.POPULAR;
        }

        Status retweet = null;
        Status targetTweet = targetRetweeter.findTargetTweet(criteria);
        String promoter = promoterAndTarget.getPromoter();
        Configuration promoterConfig = botConfig.getConfig(promoter);
        Retweeter promoterRetweeter = new Retweeter(promoterConfig);
        promoterRetweeter.unretweet();

        if (targetTweet != null) {
            long tweetId = targetTweet.getId();
            retweet = promoterRetweeter.retweet(tweetId);
        }
        return retweet;
    }
}
