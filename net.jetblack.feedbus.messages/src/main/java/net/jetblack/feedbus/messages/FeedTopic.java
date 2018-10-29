package net.jetblack.feedbus.messages;

public class FeedTopic {

	public final String Feed;
	public final String Topic;

	public FeedTopic(String feed, String topic) {
		if (feed == null) {
			throw new IllegalArgumentException("feed");
		}
		if (topic == null) {
			throw new IllegalArgumentException("topic");
		}

		Feed = feed;
		Topic = topic;
	}

	public boolean Equals(FeedTopic other) {
		return Feed == other.Feed && Topic == other.Topic;
	}

	@Override
	public int hashCode() {
		return Feed.hashCode() ^ Topic.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FeedTopic && equals((FeedTopic) obj);
	}

	public boolean equals(FeedTopic other) {
		return other != null && Feed.equals(other.Feed) && Topic.equals(other.Topic);
	}

	@Override
	public String toString() {
		return "Feed=" + Feed + ", Topic=" + Topic;
	}
}
