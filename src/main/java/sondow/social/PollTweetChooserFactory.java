package sondow.social;

import twitter4j.CardsTwitterImpl;
import twitter4j.auth.Authorization;
import twitter4j.auth.AuthorizationFactory;
import twitter4j.conf.Configuration;

public class PollTweetChooserFactory {

    public PollTweetChooser build(Configuration pollReadingConfig, Time time) {
        Authorization pollReadingAuth = AuthorizationFactory.getInstance(pollReadingConfig);
        CardsTwitterImpl cardsTwitter = new CardsTwitterImpl(pollReadingConfig, pollReadingAuth);
        return new PollTweetChooser(cardsTwitter, time);
    }
}
