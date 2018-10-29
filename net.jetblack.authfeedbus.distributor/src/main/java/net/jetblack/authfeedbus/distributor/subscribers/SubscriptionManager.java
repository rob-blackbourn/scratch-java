package net.jetblack.authfeedbus.distributor.subscribers;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetblack.authfeedbus.distributor.AuthorizationInfo;
import net.jetblack.authfeedbus.distributor.interactors.AuthorizationResponseEventArg;
import net.jetblack.authfeedbus.distributor.interactors.Interactor;
import net.jetblack.authfeedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorManager;
import net.jetblack.authfeedbus.distributor.notifiers.NotificationEventArgs;
import net.jetblack.authfeedbus.distributor.notifiers.NotificationManager;
import net.jetblack.authfeedbus.distributor.publishers.PublisherManager;
import net.jetblack.authfeedbus.distributor.publishers.StalePublisherEventArgs;
import net.jetblack.authfeedbus.distributor.roles.Role;
import net.jetblack.authfeedbus.messages.FeedTopic;
import net.jetblack.authfeedbus.messages.ForwardedMulticastData;
import net.jetblack.authfeedbus.messages.ForwardedSubscriptionRequest;
import net.jetblack.authfeedbus.messages.MulticastData;
import net.jetblack.authfeedbus.messages.SubscriptionRequest;
import net.jetblack.authfeedbus.messages.UnicastData;
import net.jetblack.util.Enumerable;

public class SubscriptionManager {

	private static final Logger logger = Logger.getLogger(SubscriptionManager.class.getName());

	private final SubscriptionRepository _repository;
	private final InteractorManager _interactorManager;
	private final NotificationManager _notificationManager;
	private final PublisherManager _publisherManager;

	public SubscriptionManager(InteractorManager interactorManager, NotificationManager notificationManager) {
		_repository = new SubscriptionRepository();

		_interactorManager = interactorManager;
		_notificationManager = notificationManager;
		_publisherManager = new PublisherManager(_interactorManager);

		interactorManager.ClosedInteractors.add(args -> onClosedInteractor(args));
		interactorManager.FaultedInteractors.add(args -> onFaultedInteractor(args));
		interactorManager.AuthorizationResponses.add(args -> onAuthorizationResponse(args));
		notificationManager.NewNotificationRequests.add(args -> onNewNotificationRequest(args));

		_publisherManager.StalePublishers.add(args -> onStaleFeedTopics(args));
	}

	public void requestSubscription(Interactor subscriber, SubscriptionRequest subscriptionRequest) {
		if (!subscriber.hasRole(subscriptionRequest.Feed, Role.Subscribe)) {
			logger.warning(
					"Rejected request from " + subscriber + " to subscribe to feed \"" + subscriptionRequest.Feed + "\""
			);
			return;
		}

		logger.fine("Received subscription from " + subscriber + " on \"" + subscriptionRequest + "\"");

		if (subscriptionRequest.IsAdd)
			_interactorManager.requestAuthorisation(subscriber, subscriptionRequest.Feed, subscriptionRequest.Topic);
		else {
			_repository.removeSubscription(subscriber, subscriptionRequest.Feed, subscriptionRequest.Topic, false);
			_notificationManager.forwardSubscription(subscriber, subscriptionRequest);
		}
	}

	public void onFaultedInteractor(InteractorFaultedEventArgs args) {
		logger.log(Level.FINE, "Interactor faulted: " + args.Interactor, args.Error);

		closeInteractor(args.Interactor);
	}

	public void onClosedInteractor(InteractorClosedEventArgs args) {
		closeInteractor(args.Interactor);
	}

	private void closeInteractor(Interactor interactor) {
		logger.fine("Removing subscriptions for " + interactor);

		// Remove the subscriptions
		Set<FeedTopic> feedTopics = _repository.findByInteractor(interactor);
		for (FeedTopic feedTopic : feedTopics)
			_repository.removeSubscription(interactor, feedTopic.Feed, feedTopic.Topic, true);

		// Inform those interested that this interactor is no longer subscribed to these
		// topics.
		for (FeedTopic feedTopic : feedTopics) // .Select(feedTopic => new SubscriptionRequest(feedTopic.Feed,
												// feedTopic.Topic, false)))
			_notificationManager
					.forwardSubscription(interactor, new SubscriptionRequest(feedTopic.Feed, feedTopic.Topic, false));
	}

	public void onAuthorizationResponse(AuthorizationResponseEventArg args) {
		if (
			args.Response.IsAuthorizationRequired
					&& (args.Response.Entitlements == null || args.Response.Entitlements.length == 0)
			) {
			var message = new ForwardedMulticastData(null, null, args.Response.Feed, args.Response.Topic, true, null);
			try {
				args.Requester.sendMessage(message);
			} catch (Exception error) {
				logger.log(Level.FINE, "Failed to send to " + args.Requester + " multi cast message " + message, error);
			}

			return;
		}

		Set<UUID> entitlements = new HashSet<UUID>();
		for (UUID entitlement : entitlements) {
			entitlements.add(entitlement);
		}

		_repository.addSubscription(
				args.Requester, args.Response.Feed, args.Response.Topic,
				new AuthorizationInfo(args.Response.IsAuthorizationRequired, entitlements));
				_notificationManager.forwardSubscription(
						args.Requester, new SubscriptionRequest(args.Response.Feed, args.Response.Topic, true));
						}

	public void sendUnicastData(Interactor publisher, UnicastData unicastData) {
		// Can we find this client in the subscribers to this topic?
		var subscriber = Enumerable.create(
				_repository.getSubscribersToFeedAndTopic(unicastData.Feed, unicastData.Topic)).firstOrDefault(
						x -> x.getKey().Id == unicastData.ClientId);
						if (subscriber == null)
			return;

		_publisherManager.sendUnicastData(publisher, subscriber.getKey(), subscriber.getValue(), unicastData);
	}

	public void sendMulticastData(Interactor publisher, MulticastData multicastData) {
		_publisherManager.sendMulticastData(
				publisher, _repository.getSubscribersToFeedAndTopic(multicastData.Feed, multicastData.Topic), multicastData);
				}

	public void onNewNotificationRequest(NotificationEventArgs args) {
		// Find the subscribers whoes subscriptions match the pattern.
		for (Entry<String, Set<Interactor>> matchingSubscriptions : _repository.getSubscribersToFeed(args.Feed)) {
			String topic = matchingSubscriptions.getKey();

			// Tell the requestor about subscribers that are interested in this topic.
			for (var subscriber : matchingSubscriptions.getValue()) {
				var message = new ForwardedSubscriptionRequest(
						subscriber.User, subscriber.Address, subscriber.Id, args.Feed, topic, true);
						try {
					args.Interactor.sendMessage(message);
				} catch (Exception error) {
					logger.log(Level.FINE, "Failed to inform " + subscriber + " regarding " + message, error);
				}
			}
		}
	}

	public void onStaleFeedTopics(StalePublisherEventArgs args) {
		for (FeedTopic feedTopic : args.FeedsAndTopics) {
			onStaleFeedTopic(feedTopic);
		}
	}

	private void onStaleFeedTopic(FeedTopic staleFeedTopic) {
		// Inform subscribers by sending an image with no data.
		var staleMessage = new ForwardedMulticastData(
				null, null, staleFeedTopic.Feed, staleFeedTopic.Topic, true, null);

				for (
					Entry<Interactor, AuthorizationInfo> item : _repository
							.getSubscribersToFeedAndTopic(staleFeedTopic.Feed, staleFeedTopic.Topic)
					) // .Select(x => x.Key))
					{
			Interactor subscriber = item.getKey();

			try {
				subscriber.sendMessage(staleMessage);
			} catch (Exception error) {
				logger.log(Level.FINE, "Failed to inform " + subscriber + " of stale " + staleFeedTopic, error);
			}
		}
	}

}
