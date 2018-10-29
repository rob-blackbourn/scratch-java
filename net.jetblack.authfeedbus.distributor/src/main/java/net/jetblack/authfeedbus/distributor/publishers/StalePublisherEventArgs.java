package net.jetblack.authfeedbus.distributor.publishers;

import java.util.List;

import net.jetblack.authfeedbus.distributor.interactors.Interactor;
import net.jetblack.authfeedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.authfeedbus.messages.FeedTopic;
import net.jetblack.util.Enumerable;
import net.jetblack.util.Strings;

public class StalePublisherEventArgs extends InteractorEventArgs {

	public StalePublisherEventArgs(Interactor interactor, List<FeedTopic> feedsAndTopics) {
		super(interactor);
		FeedsAndTopics = feedsAndTopics;
	}

	public final List<FeedTopic> FeedsAndTopics;

	@Override
	public String toString() {
		return super.toString() + ", FeedsAndTopics=[" + Strings.join(",", Enumerable.create(FeedsAndTopics)) + "]";
	}
}
