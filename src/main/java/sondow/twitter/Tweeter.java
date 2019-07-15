package sondow.twitter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Interacts with the twitter API to post tweets or do other twitter actions for a given Twitter
 * account.
 *
 * @author @JoeSondow
 */
public class Tweeter {
    private Twitter twitter;

    /**
     * Creates an instance to interact with the Twitter accounts the is set up as the default for
     * this project.
     */
    public Tweeter() {
        this(new BotConfigFactory().configure());
    }

    /**
     * Creates an instance to interact with the Twitter account specified by the supplied
     * configuration.
     *
     * @param botConfig the Twitter configuration to use to authenticate
     */
    public Tweeter(BotConfig botConfig) {
        this(new TwitterFactory(botConfig.getTwitterConfig()).getInstance());
    }

    public Tweeter(Twitter twitter) {
        this.twitter = twitter;
    }

    /**
     * Posts a tweet.
     *
     * @param message the content to tweet
     * @return the full tweet object that was created
     */
    public Status tweet(String message) {
        try {
            Status status = twitter.updateStatus(message);
            String msg = "Successfully tweeted message: " + message + " with status " + status;
            System.out.println(msg);
            return status;
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
