package net.jetblack.feedbus.distributor.publishers;

import java.util.HashSet;
import java.util.Set;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.messages.FeedTopic;
import net.jetblack.util.TwoWaySet;

public class PublisherRepository {

    private final TwoWaySet<FeedTopic, Interactor> _topicsAndPublishers = new TwoWaySet<FeedTopic, Interactor>();

    public PublisherRepository() {
    }

    public void addPublisher(Interactor publisher, String feed, String topic) {
        _topicsAndPublishers.addSecondAndFirst(publisher, new FeedTopic(feed, topic));
    }

    public Set<FeedTopic> removePublisher(Interactor publisher) {
    	Set<FeedTopic> removedTopics = _topicsAndPublishers.removeSecond(publisher);
        return removedTopics != null ? removedTopics : new HashSet<FeedTopic>();
    }

}
