package sondow.social;

import java.time.Instant;
import java.util.Iterator;
import twitter4j.Card;
import twitter4j.CardsTwitterImpl;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusWithCard;
import twitter4j.TwitterException;

public class PollTweetChooser {

    private final CardsTwitterImpl pollReadingTwitter;
    private final Time time;

    public PollTweetChooser(CardsTwitterImpl pollReadingTwitter, Time time) {
        this.pollReadingTwitter = pollReadingTwitter;
        this.time = time;
    }

    public StatusWithCard findLivePoll(String screenName) {
        StatusWithCard livePoll = null;
        try {
            ResponseList<Status> userTimeline = pollReadingTwitter.getUserTimeline(screenName);
            Iterator<Status> it = userTimeline.iterator();
            while (it.hasNext() && livePoll == null) {
                Status tweet = it.next();
                if (!tweet.isRetweet()) {
                    StatusWithCard statusWithCard = pollReadingTwitter
                            .showStatusWithCard(tweet.getId());

                    Card card = statusWithCard.getCard();
                    if (card != null) {
                        Instant endDatetimeUtc = card.getEndDatetimeUtc();
                        Instant nowUtc = time.nowUtc().toInstant();
                        if (nowUtc.isBefore(endDatetimeUtc)) {
                            // This poll is still live
                            livePoll = statusWithCard;
                        }
                    }
                }
            }
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
        return livePoll;
    }
}
