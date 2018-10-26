package net.jetblack.feedbus.distributor.publishers;

import java.util.Set;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.feedbus.messages.FeedTopic;

public class StalePublisherEventArgs extends InteractorEventArgs {

    public StalePublisherEventArgs(Interactor interactor, Set<FeedTopic> feedsAndTopics) {
        super(interactor);
        FeedsAndTopics = feedsAndTopics;
    }

    public final Set<FeedTopic> FeedsAndTopics;

	@Override
	public String toString() {
	    return super.toString() + ", FeedsAndTopics=[" + FeedsAndTopics + "]";
	}
}
