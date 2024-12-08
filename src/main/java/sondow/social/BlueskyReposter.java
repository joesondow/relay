package sondow.social;

import bsky4j.Bluesky;
import bsky4j.BlueskyFactory;
import bsky4j.api.atproto.ServerResource;
import bsky4j.api.bsky.FeedResource;
import bsky4j.api.entity.atproto.server.ServerCreateSessionRequest;
import bsky4j.api.entity.atproto.server.ServerCreateSessionResponse;
import bsky4j.api.entity.bsky.feed.FeedDeleteRepostRequest;
import bsky4j.api.entity.bsky.feed.FeedGetAuthorFeedRequest;
import bsky4j.api.entity.bsky.feed.FeedGetAuthorFeedResponse;
import bsky4j.api.entity.bsky.feed.FeedRepostRequest;
import bsky4j.api.entity.share.Response;
import bsky4j.domain.Service;
import bsky4j.model.atproto.repo.RepoStrongRef;
import bsky4j.model.bsky.feed.FeedDefsFeedViewPost;
import bsky4j.model.bsky.feed.FeedDefsPostView;
import bsky4j.model.bsky.feed.FeedDefsViewerState;
import bsky4j.model.bsky.feed.FeedPost;
import bsky4j.model.share.RecordUnion;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import sondow.common.Logger;
import twitter4j.Status;

public class BlueskyReposter {

    private final Logger log = Logger.getLogger(BlueskyReposter.class);

    private final Time time;

    private final BlueskyConfig blueskyConfig;

    private final Bluesky bluesky;

    /**
     * The Json Web Token for the current authenticated bluesky session.
     */
    private String accessJwt;

    /**
     * The unit test constructor.
     *
     * @param time          source of current time
     * @param blueskyConfig the configuration for the bluesky account whose post will be reposted
     * @param bluesky       the interface to the bluesky remote service
     */
    public BlueskyReposter(Time time, BlueskyConfig blueskyConfig, Bluesky bluesky) {
        this.time = time;
        this.blueskyConfig = blueskyConfig;
        this.bluesky = bluesky;
    }

    public BlueskyReposter(BlueskyConfig targetConfig) {
        this(new Time(), targetConfig, BlueskyFactory.getInstance(Service.BSKY_SOCIAL.getUri()));
    }

    public void repost(RepoStrongRef repoStrongRef) {
        ensureAuthenticated();
        String timestamp = Time.format(time.nowUtc());
        log.info("Reposting with timestamp " + timestamp);
        FeedRepostRequest feedRepostRequest = FeedRepostRequest.builder().accessJwt(accessJwt)
                .subject(repoStrongRef).createdAt(timestamp).build();
        bluesky.feed().repost(feedRepostRequest);
    }

    /**
     * Attempts to delete all the recent retweets from the logged in account's user timeline.
     */
    public void unrepost() {

        ensureAuthenticated();

        FeedGetAuthorFeedRequest.FeedGetAuthorFeedRequestBuilder feedRequestBuilder =
                FeedGetAuthorFeedRequest.builder();

        List<FeedDefsFeedViewPost> reposts = new ArrayList<>();
        ZonedDateTime now = time.nowUtc();
        long daysAgoCutOff = 600;
        ZonedDateTime cutoffDate = now.minusDays(daysAgoCutOff);

        boolean checkingRecentPosts = true;
        // EmojiAquarium posts 8x/day, 480x in 60 days. 50 posts per page in bluesky. 480/50 ~= 10
        int maxBlueskyPages = 10;

        // To get multiple pages, use cursor returned from previous response.g
        String nextPageCursor = null;

        String blueskyFullHandle = blueskyConfig.getFullHandle();
        log.info(blueskyFullHandle);
        FeedResource feedResource = bluesky.feed();
        for (int i = 0; i < maxBlueskyPages && checkingRecentPosts; i++) {
            feedRequestBuilder.cursor(nextPageCursor);
            FeedGetAuthorFeedRequest feedRequest = feedRequestBuilder.accessJwt(accessJwt)
                    .actor(blueskyFullHandle).build();
            Response<FeedGetAuthorFeedResponse> respFeedResp = feedResource.getAuthorFeed(feedRequest);
            FeedGetAuthorFeedResponse feedResponse = respFeedResp.get();
            nextPageCursor = feedResponse.getCursor();
            //  log.info("cursor: " + nextPageCursor);
            List<FeedDefsFeedViewPost> viewPosts = feedResponse.getFeed();
            int postCountInPage = viewPosts.size();
            if (postCountInPage == 0 || nextPageCursor == null) {
                checkingRecentPosts = false; // End of timeline, stop checking.
            }
            for (FeedDefsFeedViewPost viewPost : viewPosts) {
                FeedDefsPostView post = viewPost.getPost();
                FeedDefsViewerState viewer = post.getViewer();

                if (viewer.getRepost() != null) {
                    log.info("found repost " + viewer.getRepost());
                    reposts.add(viewPost);
                }
                String indexedAt = post.getIndexedAt();
                ZonedDateTime createdDate = Time.parse(indexedAt);
                if (createdDate.isBefore(cutoffDate)) {
                    checkingRecentPosts = false;
                }
            }
        }

        // Remove all the reposts
        for (FeedDefsFeedViewPost repost : reposts) {
            FeedDefsPostView post = repost.getPost();
            String repostUri = post.getViewer().getRepost();

            RecordUnion record = post.getRecord();
            if (record instanceof FeedPost) {
                FeedPost feedPost = (FeedPost) record;
                String text = feedPost.getText();

                log.info("Found repost to remove: " + text);

                FeedDeleteRepostRequest deleteRepostRequest = FeedDeleteRepostRequest.builder()
                        .accessJwt(accessJwt).uri(repostUri).build();
                feedResource.deleteRepost(deleteRepostRequest);
            }
        }
    }

    private void ensureAuthenticated() {
        if (accessJwt == null) {
            ServerResource server = bluesky.server();
            ServerCreateSessionRequest.ServerCreateSessionRequestBuilder builder =
                    ServerCreateSessionRequest.builder();
            String blueskyFullHandle = blueskyConfig.getFullHandle();
            String appPassword = blueskyConfig.getAppPassword();
            ServerCreateSessionRequest sessionRequest = builder.identifier(blueskyFullHandle)
                    .password(appPassword).build();
            Response<ServerCreateSessionResponse> response = server.createSession(sessionRequest);

            accessJwt = response.get().getAccessJwt();
        }
    }

    public FeedDefsFeedViewPost findTargetPopularPost() {
        ensureAuthenticated();

        FeedGetAuthorFeedRequest.FeedGetAuthorFeedRequestBuilder feedRequestBuilder =
                FeedGetAuthorFeedRequest.builder();
        FeedResource feedResource = bluesky.feed();
        String blueskyFullHandle = blueskyConfig.getFullHandle();
        FeedGetAuthorFeedRequest feedRequest = feedRequestBuilder.accessJwt(accessJwt)
                .actor(blueskyFullHandle).build();
        Response<FeedGetAuthorFeedResponse> respFeedResp = feedResource.getAuthorFeed(feedRequest);
        FeedGetAuthorFeedResponse feedResponse = respFeedResp.get();
        List<FeedDefsFeedViewPost> viewPosts = feedResponse.getFeed();
        int highestRepostCount = 0;
        int highestLikeCountForMostReposted = 0;
        FeedDefsFeedViewPost mostPopularViewPost = null;
        for (FeedDefsFeedViewPost viewPost : viewPosts) {
            FeedDefsPostView post = viewPost.getPost();
            FeedDefsViewerState viewer = post.getViewer();
            if (viewer.getRepost() == null) {
                // Found an original post.
                Integer repostCount = post.getRepostCount();
                Integer likeCount = post.getLikeCount();
                if (repostCount > highestRepostCount) {
                    mostPopularViewPost = viewPost;
                    highestRepostCount = repostCount;
                    highestLikeCountForMostReposted = likeCount;
                } else if (repostCount == highestRepostCount) {
                    if (likeCount > highestLikeCountForMostReposted) {
                        highestLikeCountForMostReposted = likeCount;
                        mostPopularViewPost = viewPost;
                    }
                }
            }
        }
        return mostPopularViewPost;
    }
}
