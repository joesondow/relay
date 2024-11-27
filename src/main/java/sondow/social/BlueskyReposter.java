package sondow.social;

import bsky4j.Bluesky;
import bsky4j.BlueskyFactory;
import bsky4j.api.atproto.ServerResource;
import bsky4j.api.bsky.FeedResource;
import bsky4j.api.entity.atproto.server.ServerCreateSessionRequest;
import bsky4j.api.entity.atproto.server.ServerCreateSessionRequest.ServerCreateSessionRequestBuilder;
import bsky4j.api.entity.atproto.server.ServerCreateSessionResponse;
import bsky4j.api.entity.bsky.feed.FeedGetAuthorFeedRequest;
import bsky4j.api.entity.bsky.feed.FeedGetAuthorFeedResponse;
import bsky4j.api.entity.bsky.feed.FeedGetPostsRequest;
import bsky4j.api.entity.bsky.feed.FeedGetPostsResponse;
import bsky4j.api.entity.bsky.feed.FeedPostRequest;
import bsky4j.api.entity.bsky.feed.FeedPostResponse;
import bsky4j.api.entity.bsky.feed.FeedRepostRequest;
import bsky4j.api.entity.bsky.feed.FeedRepostResponse;
import bsky4j.api.entity.share.Response;
import bsky4j.domain.Service;
import bsky4j.model.atproto.repo.RepoStrongRef;
import bsky4j.model.bsky.feed.FeedDefsFeedViewPost;
import bsky4j.model.bsky.feed.FeedDefsPostView;
import bsky4j.model.bsky.feed.FeedDefsViewerState;
import bsky4j.model.bsky.feed.FeedPost;
import bsky4j.model.share.RecordUnion;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import sondow.common.Logger;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class BlueskyReposter {

    private Logger log = Logger.getLogger(BlueskyReposter.class);

    private final Time time;

    private BlueskyConfig blueskyConfig;

    /**
     * The unit test constructor.
     *
     * @param time    source of current time
     * @param blueskyConfig the configuration for the bluesky account whose post will be reposted
     */
    public BlueskyReposter(Time time, BlueskyConfig blueskyConfig) {
        this.time = time;
        this.blueskyConfig = blueskyConfig;
    }

    public BlueskyReposter(BlueskyConfig targetConfig) {
        this(new Time(), targetConfig);
    }

    public void repost() {
    }

    /**
     * Attempts to delete all the recent retweets from the logged in account's user timeline.
     */
    public void unrepost() {

        String bskyUri = Service.BSKY_SOCIAL.getUri();
        Bluesky bluesky = BlueskyFactory.getInstance(bskyUri);
        ServerResource server = BlueskyFactory.getInstance(bskyUri).server();
        ServerCreateSessionRequest.ServerCreateSessionRequestBuilder builder = ServerCreateSessionRequest.builder();
        String blueskyFullHandle = blueskyConfig.getFullHandle();
        String appPassword = blueskyConfig.getAppPassword();
        ServerCreateSessionRequest sessionRequest = builder.identifier(blueskyFullHandle)
                .password(appPassword).build();
        Response<ServerCreateSessionResponse> response = server.createSession(sessionRequest);

        String accessJwt = response.get().getAccessJwt();

        FeedGetAuthorFeedRequest.FeedGetAuthorFeedRequestBuilder feedRequestBuilder = FeedGetAuthorFeedRequest.builder();

        List<FeedDefsFeedViewPost> reposts = new ArrayList<>();
        ZonedDateTime now = time.nowUtc();
        long daysAgoCutOff = 600;
        ZonedDateTime cutoffDate = now.minus(daysAgoCutOff, ChronoUnit.DAYS);

        boolean checkingRecentPosts = true;
        // EmojiAquarium posts 8x/day, 480x in 60 days. 50 posts per page in bluesky. 480/50 ~= 10
        int maxBlueskyPages = 10;

        // To get multiple pages, use cursor returned from previous response.g
        String nextPageCursor = null;

        log.info(blueskyFullHandle);
        for (int i = 0; i < maxBlueskyPages && checkingRecentPosts; i++) {
            feedRequestBuilder.cursor(nextPageCursor);
            FeedGetAuthorFeedRequest feedRequest = feedRequestBuilder.accessJwt(accessJwt)
                    .actor(blueskyFullHandle).build();
            Response<FeedGetAuthorFeedResponse> feeds = bluesky.feed().getAuthorFeed(feedRequest);
            FeedGetAuthorFeedResponse feedResponse = feeds.get();
            nextPageCursor = feedResponse.getCursor();
            log.info("cursor: " + nextPageCursor);
            List<FeedDefsFeedViewPost> viewPosts = feedResponse.getFeed();
            int postCountInPage = viewPosts.size();
            if (postCountInPage <= 0) {
                checkingRecentPosts = false; // End of timeline, stop checking.
            }
            for (int p = 0; p < viewPosts.size(); p++) {
                FeedDefsFeedViewPost viewPost = viewPosts.get(p);
                FeedDefsPostView post = viewPost.getPost();
                FeedDefsViewerState viewer = post.getViewer();
                if (viewer.getRepost() != null) {
                    log.info("found repost " + viewPost.getPost());
                    reposts.add(viewPost);
                }
                String indexedAt = post.getIndexedAt();
                ZonedDateTime createdDate = Time.parse(indexedAt);
                if (createdDate.isBefore(cutoffDate)) {
                    checkingRecentPosts = false;
                }
            }
        }

        for (FeedDefsFeedViewPost repost: reposts) {
            FeedDefsPostView post = repost.getPost();
            RecordUnion record = post.getRecord();
            if (record instanceof FeedPost) {
                FeedPost feedPost = (FeedPost) record;
                String text = feedPost.getText();
                
                log.info(text);
            }

        }

        //            try {
        //
        //                ResponseList<Status> userTimeline = twitter.getUserTimeline(paging);
        //                int tweetCountInPage = userTimeline.size();
        //                if (tweetCountInPage <= 0) {
        //                    checkingRecentTweets = false; // End of timeline, stop checking.
        //                }
        //                for (int u = 0; u < tweetCountInPage && checkingRecentTweets; u++) {
        //                    Status tweet = userTimeline.get(u);
        //                    if (tweet.isRetweet()) {
        //                        retweets.add(tweet);
        //                    }
        //                    if (maxId == null) {
        //                        maxId = tweet.getId();
        //                    } else {
        //                        maxId = Math.min(maxId, tweet.getId());
        //                    }
        //                    paging = new Paging();
        //                    paging.setMaxId(maxId - 1);
        //
        //                    Date createdAt = tweet.getCreatedAt();
        //                    if (createdAt.before(cutoffDate)) {
        //                        checkingRecentTweets = false;
        //                    }
        //                }
        //            } catch (TwitterException e) {
        //                throw new RuntimeException(e);
        //            }
        //    }
        //        for (Status retweet : retweets) {
        //            Status retweetedStatus = retweet.getRetweetedStatus();
        //            String text = retweetedStatus.getText();
        //            String screenName = retweetedStatus.getUser().getScreenName();
        //            try {
        //                long id = retweet.getId();
        //                log.info("Deleting " + config.getUser() + " retweet " + id + " of " + screenName +
        //                        " " +
        //                        retweet.getRetweetedStatus().getId() + " with text '" + text + "'");
        //                twitter.destroyStatus(id);
        //            } catch (TwitterException e) {
        //                throw new RuntimeException(e);
        //            }
        //        }
    }

    public void findTargetPopularPost() {}
}
