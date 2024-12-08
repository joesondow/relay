package sondow.social;

import bsky4j.model.atproto.repo.RepoStrongRef;
import bsky4j.model.bsky.feed.FeedDefsFeedViewPost;
import bsky4j.model.bsky.feed.FeedDefsPostView;
import bsky4j.model.bsky.feed.FeedPost;
import bsky4j.model.share.RecordUnion;
import sondow.common.Logger;
import twitter4j.Status;
import twitter4j.conf.Configuration;

/**
 * The main application logic.
 */
public class Bot {

    private final Logger log = Logger.getLogger(Bot.class);

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
    public void go() {

        // Find this account's retweets dating back two weeks or 60 tweets.
        AccountChooser accountChooser = new AccountChooser(botConfig);

        PromoterAndTarget twitterPromoterAndTarget;
        String twitterTargetScreenName = null;
        Retweeter twitterPromoterRetweeter = null;
        boolean doingTwitter = false;
        if (doingTwitter) {
            twitterPromoterAndTarget = accountChooser.chooseTwitterPromoterAndTarget();
            twitterTargetScreenName = twitterPromoterAndTarget.getTarget();

            String twitterPromoter = twitterPromoterAndTarget.getPromoter();
            Configuration twitterPromoterConfig = botConfig.getTwitterConfig(twitterPromoter);
            twitterPromoterRetweeter = retweeterFactory.build(twitterPromoterConfig);
            twitterPromoterRetweeter.unretweet();
        }
        PromoterAndTarget blueskyPromoterAndTarget = accountChooser.chooseBlueskyPromoterAndTarget();
        String blueskyTargetShortName = blueskyPromoterAndTarget.getTarget();
        String blueSkyPromoterShortName = blueskyPromoterAndTarget.getPromoter();
        BlueskyConfig blueskyPromoterConfig = botConfig.getBlueskyConfig(blueSkyPromoterShortName);
        BlueskyReposter blueskyPromoterReposter = blueskyReposterFactory.build(blueskyPromoterConfig);
        blueskyPromoterReposter.unrepost();


        Status retweet = null;
        Long tweetId = null;
//        if (botConfig.isPollAccount(twitterTargetScreenName)) {
//            Configuration pollReadingConfig = botConfig.getPollReadingTwitterConfig();
//            PollTweetChooser pollTweetChooser = pollTweetChooserFactory.build(pollReadingConfig, time);
//            StatusWithCard pollTweet = pollTweetChooser.findLivePoll(targetScreenName);
//            if (pollTweet != null) {
//                tweetId = pollTweet.getId();
//            }
//        } else {
        if (doingTwitter) {
            Configuration twitterTargetConfig = botConfig.getTwitterConfig(twitterTargetScreenName);
            Retweeter twitterTargetRetweeter = retweeterFactory.build(twitterTargetConfig);
            Status targetTweet = twitterTargetRetweeter.findTargetPopularTweet();
            if (targetTweet != null) {
                tweetId = targetTweet.getId();
            }
            if (tweetId != null) {
                retweet = twitterPromoterRetweeter.retweet(tweetId);
            }
        }

        BlueskyConfig blueskyTargetConfig = botConfig.getBlueskyConfig(blueskyTargetShortName);
        BlueskyReposter blueskyTargetReposter = blueskyReposterFactory.build(blueskyTargetConfig);
        FeedDefsFeedViewPost targetViewPost = blueskyTargetReposter.findTargetPopularPost();
        if (targetViewPost != null) {
            FeedDefsPostView post = targetViewPost.getPost();
            String uri = post.getUri();
            String cid = post.getCid();
            RepoStrongRef repoStrongRef = new RepoStrongRef(uri, cid);
            RecordUnion record = post.getRecord();
            if (record instanceof FeedPost) {
                FeedPost feedPost = (FeedPost) record;
                String text = feedPost.getText();

                log.info(blueSkyPromoterShortName + " Reposting: cid=" + cid +"  uri=" + uri + "  text=" + text);
            }

            blueskyPromoterReposter.repost(repoStrongRef);
        }


        //        }

//        return retweet;
    }
}
