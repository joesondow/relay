package sondow.social;

import twitter4j.Status;
import twitter4j.conf.Configuration;

/**
 * The main application logic.
 */
public class Bot {

    private final BotConfig botConfig;
    private final RetweeterFactory retweeterFactory;
    private final Time time;
    private final PollTweetChooserFactory pollTweetChooserFactory;

    Bot(BotConfigFactory botConfigFactory, RetweeterFactory retweeterFactory, Time time, PollTweetChooserFactory pollTweetChooserFactory) {
        this.botConfig = botConfigFactory.configure();
        this.retweeterFactory = retweeterFactory;
        this.time = time;
        this.pollTweetChooserFactory = pollTweetChooserFactory;
    }

    Bot() {
        this(new BotConfigFactory(), new RetweeterFactory(), new Time(), new PollTweetChooserFactory());
    }

    /**
     * Performs the application logic.
     *
     * @return the posted tweet
     */
    public Status go() {

        // Find this account's retweets dating back two weeks or 60 tweets.
        AccountChooser accountChooser = new AccountChooser(botConfig);
        PromoterAndTarget twitterPromoterAndTarget = accountChooser.chooseTwitterPromoterAndTarget();
        String targetScreenName = twitterPromoterAndTarget.getTarget();

        String twitterPromoter = twitterPromoterAndTarget.getPromoter();
        Configuration twitterPromoterConfig = botConfig.getTwitterConfig(twitterPromoter);
        Retweeter twitterPromoterRetweeter = retweeterFactory.build(twitterPromoterConfig);
        twitterPromoterRetweeter.unretweet();

        Status retweet = null;
        Long tweetId = null;
        if (botConfig.isPollAccount(targetScreenName)) {
//            Configuration pollReadingConfig = botConfig.getPollReadingTwitterConfig();
//            PollTweetChooser pollTweetChooser = pollTweetChooserFactory.build(pollReadingConfig, time);
//            StatusWithCard pollTweet = pollTweetChooser.findLivePoll(targetScreenName);
//            if (pollTweet != null) {
//                tweetId = pollTweet.getId();
//            }
        } else {
            Configuration targetConfig = botConfig.getTwitterConfig(targetScreenName);
            Retweeter targetRetweeter = retweeterFactory.build(targetConfig);
            Status targetTweet = targetRetweeter.findTargetPopularTweet();
            if (targetTweet != null) {
                tweetId = targetTweet.getId();
            }
        }
        if (tweetId != null) {
            retweet = twitterPromoterRetweeter.retweet(tweetId);
        }
        return retweet;
    }
}
