package net.jetblack.feedbus.messages;

public class FeedTopic {
    public final String Feed;
    public final String Topic;

    public FeedTopic(String feed, String topic) {
        Feed = feed;
        Topic = topic;
    }

    public boolean Equals(FeedTopic other) {
        return Feed == other.Feed && Topic == other.Topic;
    }

    @Override
    public int hashCode() {
        return (Feed != null ? Feed : "").hashCode() ^ (Topic != null ? Topic : "").hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FeedTopic && equals((FeedTopic)obj);
    }
    
    public boolean equals(FeedTopic other) {
    	return other != null && Feed.equals(other.Feed) && Topic.equals(other.Topic);
    }

    @Override
    public String toString() {
        return "Feed=" + Feed + ", Topic=" + Topic;
    }
}

