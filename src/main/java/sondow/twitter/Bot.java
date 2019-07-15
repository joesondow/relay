package sondow.twitter;

import twitter4j.Status;

/**
 * The main application logic.
 */
public class Bot {

    /**
     * The object that does the tweeting.
     */
    private Tweeter tweeter;

    /**
     * Constructs a new bot with the specified Tweeter, for unit testing.
     *
     * @param tweeter the object that tweets
     */
    Bot(Tweeter tweeter) {
        this.tweeter = tweeter;
    }

    /**
     * Constructs a new bot.
     */
    Bot() {
        this(new Tweeter());
    }

    /**
     * Performs the application logic.
     *
     * @return the posted tweet
     */
    public Status go() {

        return tweeter.tweet("");
    }
}
