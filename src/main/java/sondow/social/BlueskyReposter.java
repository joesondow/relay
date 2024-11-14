package sondow.social;

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
import bsky4j.model.bsky.feed.FeedDefsPostView;
import java.util.List;
import twitter4j.Status;

public class BlueskyReposter {

    private BlueskyConfig targetConfig;

    public BlueskyReposter(BlueskyConfig targetConfig) {
        this.targetConfig = targetConfig;
    }

    public void repost() {
    }

    public void unrepost() {}
}
