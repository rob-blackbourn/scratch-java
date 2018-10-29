package net.jetblack.authfeedbus.distributor.subscribers;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.jetblack.authfeedbus.distributor.AuthorizationInfo;
import net.jetblack.authfeedbus.distributor.interactors.Interactor;
import net.jetblack.authfeedbus.messages.FeedTopic;

public class SubscriptionRepository {

	// Feed->Topic->Interactor->SubscriptionCount.
	private final Map<String, Map<String, Map<Interactor, SubscriptionState>>> _cache = new HashMap<String, Map<String, Map<Interactor, SubscriptionState>>>();

	public SubscriptionRepository() {
	}

	public void addSubscription(Interactor subscriber, String feed, String topic, AuthorizationInfo authorizationInfo) {
		// Find topic subscriptions for this feed.
		Map<String, Map<Interactor, SubscriptionState>> topicCache = _cache.get(feed);
		if (topicCache == null)
			_cache.put(feed, topicCache = new HashMap<String, Map<Interactor, SubscriptionState>>());

		// Find subscribers to this topic.
		Map<Interactor, SubscriptionState> subscribersForTopic = topicCache.get(topic);
		if (subscribersForTopic == null)
			topicCache.put(topic, subscribersForTopic = new HashMap<Interactor, SubscriptionState>());

		// Find the subscription state for this subscriber.
		SubscriptionState subscriptionState = subscribersForTopic.get(subscriber);
		if (subscriptionState == null)
			subscribersForTopic.put(subscriber, subscriptionState = new SubscriptionState(authorizationInfo));

		// Increment the subscription count.
		subscriptionState.Count = subscriptionState.Count + 1;
	}

	public void removeSubscription(Interactor subscriber, String feed, String topic, boolean removeAll) {
		// Can we find topic subscriptions for this feed?
		Map<String, Map<Interactor, SubscriptionState>> topicCache = _cache.get(feed);
		if (topicCache == null)
			return;

		// Can we find subscribers for this topic?
		Map<Interactor, SubscriptionState> subscribersForTopic = topicCache.get(topic);
		if (subscribersForTopic == null)
			return;

		// Can we find a subscription state for this subscriber?
		SubscriptionState subscriptionState = subscribersForTopic.get(subscriber);
		if (subscriptionState == null)
			return;

		// If we are removing all subscribers, or this subscriber has no active
		// subscriptions, remove the subscriber.
		if (removeAll || --subscriptionState.Count == 0)
			subscribersForTopic.remove(subscriber);

		// If there are no subscribers left on this topic, remove it from the feed.
		if (subscribersForTopic.isEmpty())
			topicCache.remove(topic);

		// If there are no topics left in the feed, remove it from the cache.
		if (topicCache.isEmpty())
			_cache.remove(feed);
	}

	public Set<FeedTopic> findByInteractor(Interactor interactor) {
		var feedTopics = new HashSet<FeedTopic>();
		
		for (Entry<String, Map<String, Map<Interactor, SubscriptionState>>> feedTopicCache : _cache.entrySet()) {
			for (Entry<String, Map<Interactor, SubscriptionState>> topicCache : feedTopicCache.getValue().entrySet()) {
				Map<Interactor, SubscriptionState> interactorStates = topicCache.getValue();
				if (interactorStates.containsKey(interactor)) {
					feedTopics.add(new FeedTopic(feedTopicCache.getKey(), topicCache.getKey()));
				}
				
			}
		}
		
		return feedTopics;
	}

	public List<Map.Entry<Interactor, AuthorizationInfo>> getSubscribersToFeedAndTopic(
			String feed, String topic
	) {
		List<Map.Entry<Interactor, AuthorizationInfo>> subscribersToFeedAndTopic = new ArrayList<Map.Entry<Interactor, AuthorizationInfo>>();
		
		// Can we find this feed in the cache?
		Map<String, Map<Interactor, SubscriptionState>> topicCache = _cache.get(feed);
		if (topicCache != null) {
			// Are there subscribers for this topic?
			Map<Interactor, SubscriptionState> subscribersForTopic = topicCache.get(topic);
			if (subscribersForTopic != null) {
				for (var x : subscribersForTopic.entrySet()) {
					subscribersToFeedAndTopic.add(
							new AbstractMap.SimpleImmutableEntry<Interactor, AuthorizationInfo>(
									x.getKey(),
									x.getValue().AuthorizationState));
				}
			}
		}

		return subscribersToFeedAndTopic;
	}

	public List<Map.Entry<String, Set<Interactor>>> getSubscribersToFeed(String feed) {
		var items = new ArrayList<Map.Entry<String, Set<Interactor>>>();

		// Can we find this feed in the cache?
		Map<String, Map<Interactor, SubscriptionState>> topicCache = _cache.get(feed);
		if (topicCache != null) {
			for (var x : topicCache.entrySet()) {
				items.add(
						new AbstractMap.SimpleImmutableEntry<String, Set<Interactor>>(x.getKey(), x.getValue().keySet())
				);
			}
		}

		return items;
	}

	private static class SubscriptionState {
		public SubscriptionState(AuthorizationInfo authorizationInfo) {
			AuthorizationState = authorizationInfo;
		}

		public int Count;
		public final AuthorizationInfo AuthorizationState;
		public Set<UUID> Entitlements;
	}
}
