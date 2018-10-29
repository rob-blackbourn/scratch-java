package net.jetblack.authfeedbus.distributor.publishers;

import java.util.Set;

import net.jetblack.authfeedbus.distributor.interactors.Interactor;
import net.jetblack.authfeedbus.messages.FeedTopic;
import net.jetblack.util.TwoWaySet;

public class PublisherRepository {
	private final TwoWaySet<FeedTopic, Interactor> _topicsAndPublishers = new TwoWaySet<FeedTopic, Interactor>();

	public PublisherRepository() {
	}

	public void addPublisher(Interactor publisher, String feed, String topic) {
		_topicsAndPublishers.addSecondAndFirst(publisher, new FeedTopic(feed, topic));
	}

	public Set<FeedTopic> RemovePublisher(Interactor publisher) {
		return _topicsAndPublishers.removeSecond(publisher);
	}
}
