package sondow.social;

import twitter4j.Status;
import twitter4j.conf.Configuration;

/**
 * The main application logic.
 */
public class Bot {

    private final BotConfig botConfig;
    private final RetweeterFactory retweeterFactory;

    private final BlueskyReposterFactory blueskyReposterFactory;
    private final Time time;
    private final PollTweetChooserFactory pollTweetChooserFactory;

    Bot(BotConfigFactory botConfigFactory, RetweeterFactory retweeterFactory,
            BlueskyReposterFactory blueskyReposterFactory, Time time,
            PollTweetChooserFactory pollTweetChooserFactory) {
        this.botConfig = botConfigFactory.configure();
        this.retweeterFactory = retweeterFactory;
        this.blueskyReposterFactory = blueskyReposterFactory;
        this.time = time;
        this.pollTweetChooserFactory = pollTweetChooserFactory;
    }

    Bot() {
        this(new BotConfigFactory(), new RetweeterFactory(), new BlueskyReposterFactory(),
                new Time(), new PollTweetChooserFactory());
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

        String twitterTargetScreenName = twitterPromoterAndTarget.getTarget();

        String twitterPromoter = twitterPromoterAndTarget.getPromoter();
        Configuration twitterPromoterConfig = botConfig.getTwitterConfig(twitterPromoter);
        Retweeter twitterPromoterRetweeter = retweeterFactory.build(twitterPromoterConfig);
        twitterPromoterRetweeter.unretweet();

        PromoterAndTarget blueskyPromoterAndTarget = accountChooser.chooseBlueskyPromoterAndTarget();
        String blueskyTargetShortName = blueskyPromoterAndTarget.getTarget();
        String blueSkyPromoterShortName = blueskyPromoterAndTarget.getPromoter();
        BlueskyConfig blueskyPromoterConfig = botConfig.getBlueskyConfig(blueSkyPromoterShortName);
        BlueskyReposter blueskyReposter = blueskyReposterFactory.build(blueskyPromoterConfig);
        blueskyReposter.unrepost();

        Status retweet = null;
        Long tweetId = null;
        if (botConfig.isPollAccount(twitterTargetScreenName)) {
//            Configuration pollReadingConfig = botConfig.getPollReadingTwitterConfig();
//            PollTweetChooser pollTweetChooser = pollTweetChooserFactory.build(pollReadingConfig, time);
//            StatusWithCard pollTweet = pollTweetChooser.findLivePoll(targetScreenName);
//            if (pollTweet != null) {
//                tweetId = pollTweet.getId();
//            }
        } else {
            Configuration targetConfig = botConfig.getTwitterConfig(twitterTargetScreenName);
            Retweeter twitterTargetRetweeter = retweeterFactory.build(targetConfig);
            Status targetTweet = twitterTargetRetweeter.findTargetPopularTweet();
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
