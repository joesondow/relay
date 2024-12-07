package sondow.social

import bsky4j.Bluesky
import bsky4j.api.atproto.ServerResource
import bsky4j.api.bsky.FeedResource
import bsky4j.api.entity.atproto.server.ServerCreateSessionResponse
import bsky4j.api.entity.bsky.feed.FeedGetAuthorFeedResponse
import bsky4j.api.entity.share.Response
import bsky4j.model.bsky.feed.FeedDefsFeedViewPost
import bsky4j.model.bsky.feed.FeedDefsPostView
import bsky4j.model.bsky.feed.FeedDefsViewerState
import bsky4j.model.bsky.feed.FeedPost
import java.time.ZonedDateTime
import spock.lang.Specification

class BlueskyReposterSpec extends Specification {

//    def 'should repost'() {
//        setup:
//        Time time = Mock()
//        Configuration config = Mock()
//        Twitter twitter = Mock()
//        Retweeter retweeter = new Retweeter(time, config, twitter)
//        Status tweet = Mock()
//        Status retweeted = Mock()
//        User user = Mock()
//
//        when:
//        Status result = retweeter.retweet(1234L)
//
//        then:
//        result.is(tweet)
//        1 * twitter.retweetStatus(1234L) >> tweet
//        1 * config.getUser() >> "Bob"
//        1 * tweet.getRetweetedStatus() >> retweeted
//        1 * retweeted.getUser() >> user
//        1 * user.getScreenName() >> "Sue"
//        0 * _._
//    }

    private static FeedDefsFeedViewPost makePost() {
        FeedDefsPostView postView = new FeedDefsPostView(viewer: new FeedDefsViewerState(), indexedAt: '2024-11-29T23:22:33.620Z')
        new FeedDefsFeedViewPost(post: postView)
    }

    private static FeedDefsFeedViewPost makeRepost() {
        FeedDefsPostView postView = new FeedDefsPostView(viewer: new FeedDefsViewerState(repost: 'uriofrepost'), indexedAt: '2024-11-29T23:22:33.620Z', record: new FeedPost(text: "So good it's worth reposting"))
        new FeedDefsFeedViewPost(post: postView)
    }

    def 'should unrepost recent repost in short list of posts'() {
        setup:
        Time time = Mock()
        BlueskyConfig blueskyConfig = new BlueskyConfig('ghost.boo', 'Casper', '1234-abcd')
        Bluesky bluesky = Mock()

        BlueskyReposter blueskyReposter = new BlueskyReposter(time, blueskyConfig, bluesky)
        ServerResource server = Mock()
        Response<ServerCreateSessionResponse> createSessionResp = Mock()
        ServerCreateSessionResponse createSessionResponse = Mock()
        FeedResource feedResource = Mock()
        Response<FeedGetAuthorFeedResponse> respFeedResp = Mock()
        FeedGetAuthorFeedResponse feedResponse = Mock()

        FeedDefsFeedViewPost viewPost1 = makePost()
        FeedDefsFeedViewPost viewPostRepost = makeRepost()
        FeedDefsFeedViewPost viewPost3 = makePost()
        List<FeedDefsFeedViewPost> viewPosts = [viewPost1, viewPostRepost, viewPost3]

        when:
        blueskyReposter.unrepost()

        then:
        1 * bluesky.server() >> server
        1 * server.createSession({it.identifier == "casper.ghost.boo" && it.password == '1234-abcd'}) >> createSessionResp
        1 * createSessionResp.get() >> createSessionResponse
        1 * createSessionResponse.getAccessJwt() >> 'thisisthejsonwebtoken'
        1 * time.nowUtc() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
        1 * bluesky.feed() >> feedResource
        1 * feedResource.getAuthorFeed({it.actor == 'casper.ghost.boo' && it.accessJwt == 'thisisthejsonwebtoken'}) >> respFeedResp
        1 * respFeedResp.get() >> feedResponse
        1 * feedResponse.getCursor() >> null
        1 * feedResponse.getFeed() >> viewPosts
        1 * feedResource.deleteRepost({it.accessJwt == 'thisisthejsonwebtoken' && it.uri == 'uriofrepost'})
        0 * _._
    }

//    def 'should unretweet nothing if there are no tweets'() {
//        setup:
//        Time time = Mock()
//        Configuration config = Mock()
//        Twitter twitter = Mock()
//        Retweeter retweeter = new Retweeter(time, config, twitter)
//        ResponseList<Status> userTimeline = Mock()
//
//        when:
//        retweeter.unretweet()
//
//        then:
//        1 * time.nowUtc() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
//        1 * twitter.getUserTimeline(new Paging()) >> userTimeline
//        1 * userTimeline.size() >> 0
//        0 * _._
//    }
//
//    def 'should unretweet nothing if there are no retweets'() {
//        setup:
//        Time time = Mock()
//        Configuration config = Mock()
//        Twitter twitter = Mock()
//        Retweeter retweeter = new Retweeter(time, config, twitter)
//        ResponseList<Status> userTimeline1 = Mock()
//        Status tweet0 = Mock()
//        long tweet0Id = 60300
//        Date tweet0CreatedAt = Date.from(ZonedDateTime.parse('2019-06-19T05:15:20Z').toInstant())
//        Status tweet1 = Mock()
//        long tweet1Id = 39500
//        Date tweet1CreatedAt = Date.from(ZonedDateTime.parse('2019-06-17T16:40:10Z').toInstant())
//        ResponseList<Status> userTimeline2 = Mock()
//
//        when:
//        retweeter.unretweet()
//
//        then:
//        1 * time.nowUtc() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
//        1 * twitter.getUserTimeline(new Paging()) >> userTimeline1
//        1 * userTimeline1.size() >> 2
//
//        1 * userTimeline1.get(0) >> tweet0
//        1 * tweet0.isRetweet() >> false
//        1 * tweet0.getId() >> tweet0Id
//        1 * tweet0.getCreatedAt() >> tweet0CreatedAt
//
//        1 * userTimeline1.get(1) >> tweet1
//        1 * tweet1.isRetweet() >> false
//        1 * tweet1.getId() >> tweet1Id
//        1 * tweet1.getCreatedAt() >> tweet1CreatedAt
//
//        1 * twitter.getUserTimeline(new Paging(maxId: 39499L)) >> userTimeline2
//        1 * userTimeline2.size() >> 0
//
//        0 * _._
//    }
//
//    def 'should stop collecting tweets after reaching old dates'() {
//        setup:
//        Time time = Mock()
//        Configuration config = Mock()
//        Twitter twitter = Mock()
//        Retweeter retweeter = new Retweeter(time, config, twitter)
//        ResponseList<Status> userTimeline1 = Mock()
//        Status tweet0 = Mock()
//        long tweet0Id = 60300
//        Date tweet0CreatedAt = Date.from(ZonedDateTime.parse('2019-02-19T05:15:20Z').toInstant())
//
//        when:
//        retweeter.unretweet()
//
//        then:
//        1 * time.nowUtc() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
//        1 * twitter.getUserTimeline(new Paging()) >> userTimeline1
//        1 * userTimeline1.size() >> 20
//
//        1 * userTimeline1.get(0) >> tweet0
//        1 * tweet0.isRetweet() >> false
//        1 * tweet0.getId() >> tweet0Id
//        1 * tweet0.getCreatedAt() >> tweet0CreatedAt
//
//        0 * _._
//    }
//
//    def 'should stop collecting data after a large number of pages'() {
//        setup:
//        Time time = Mock()
//        Twitter twitter = Mock()
//        Retweeter retweeter = new Retweeter(time, Mock(Configuration), twitter)
//        ResponseList<Status> userTimeline1 = Mock()
//        Status tweet0 = Mock()
//        Date tweet0CreatedAt = Date.from(ZonedDateTime.parse('2019-06-19T05:15:20Z').toInstant())
//
//        when:
//        retweeter.unretweet()
//
//        then:
//        1 * time.nowUtc() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
//        24 * twitter.getUserTimeline(_) >> userTimeline1
//        24 * userTimeline1.size() >> 20
//        480 * userTimeline1.get(_) >> tweet0
//
//        480 * tweet0.isRetweet() >> false
//        480 * tweet0.getId() >> 60013
//        480 * tweet0.getCreatedAt() >> tweet0CreatedAt
//
//        0 * _._
//    }

//    def 'should find target tweet'() {
//        setup:
//        Retweeter retweeter = new Retweeter()
//    }
}
