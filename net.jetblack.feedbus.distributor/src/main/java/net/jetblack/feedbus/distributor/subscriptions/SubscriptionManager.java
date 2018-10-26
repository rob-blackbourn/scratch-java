package net.jetblack.feedbus.distributor.subscriptions;

import java.util.List;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorManager;
import net.jetblack.feedbus.distributor.notifiers.NotificationEventArgs;
import net.jetblack.feedbus.distributor.notifiers.NotificationManager;
import net.jetblack.feedbus.distributor.publishers.PublisherManager;
import net.jetblack.feedbus.distributor.publishers.StalePublisherEventArgs;
import net.jetblack.feedbus.messages.FeedTopic;
import net.jetblack.feedbus.messages.ForwardedSubscriptionRequest;
import net.jetblack.feedbus.messages.MonitorRequest;
import net.jetblack.feedbus.messages.MulticastData;
import net.jetblack.feedbus.messages.SubscriptionRequest;
import net.jetblack.feedbus.messages.UnicastData;
import net.jetblack.util.Enumerable;
import net.jetblack.util.EventListener;
import net.jetblack.util.StringComparator;
import net.jetblack.util.invokables.UnaryFunction;

public class SubscriptionManager {

	private static final Logger logger = Logger.getLogger(SubscriptionManager.class.getName());

    private final SubscriptionRepository _repository;
    private final NotificationManager _notificationManager;
    private final PublisherManager _publisherManager;

    public SubscriptionManager(InteractorManager interactorManager, NotificationManager notificationManager) {
        _notificationManager = notificationManager;

        _repository = new SubscriptionRepository();
        _publisherManager = new PublisherManager(interactorManager);

        interactorManager.InteractorClosed.add(new EventListener<InteractorClosedEventArgs>() {
			@Override
			public void onEvent(InteractorClosedEventArgs event) {
		        closeInteractor(event.Interactor);
			}
		});
        
        interactorManager.InteractorFaulted.add(new EventListener<InteractorFaultedEventArgs>() {
			@Override
			public void onEvent(InteractorFaultedEventArgs event) {
		        logger.fine("Interactor faulted: {args.Interactor} - {args.Error.Message}");
		        closeInteractor(event.Interactor);
			}
		});

        notificationManager.NewNotificationRequest.add(new EventListener<NotificationEventArgs>() {
			@Override
			public void onEvent(NotificationEventArgs event) {
		        // Find the subscribers whoes subscriptions match the pattern.
		        for (var matchingSubscriptions : _repository.getSubscribersToFeed(event.Feed)) {
		            // Tell the requestor about subscribers that are interested in this topic.
		            for (var subscriber : matchingSubscriptions.Value) {
		                try {
							event.Interactor.sendMessage(new ForwardedSubscriptionRequest(subscriber.Id, event.Feed, matchingSubscriptions.Key, true));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		        }
			}
		});

        _publisherManager.StalePublisher.add(new EventListener<StalePublisherEventArgs>() {

			@Override
			public void onEvent(StalePublisherEventArgs event) {
		        for (var staleFeedTopic : event.FeedsAndTopics) {
		            var staleMessage = new MulticastData(staleFeedTopic.Feed, staleFeedTopic.Topic, true, null);

		            for (var subscriber : _repository.GetSubscribersToFeedAndTopic(staleFeedTopic.Feed, staleFeedTopic.Topic)) {
		                try {
							subscriber.sendMessage(staleMessage);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		        }
			}
        	
        });
        
    }

    public void requestSubscription(Interactor subscriber, SubscriptionRequest subscriptionRequest) {
        logger.info("Received subscription from {subscriber} on \"{subscriptionRequest}\"");

        if (subscriptionRequest.IsAdd)
            _repository.addSubscription(subscriber, subscriptionRequest.Feed, subscriptionRequest.Topic);
        else
            _repository.removeSubscription(subscriber, subscriptionRequest.Feed, subscriptionRequest.Topic, false);

        _notificationManager.forwardSubscription(new ForwardedSubscriptionRequest(subscriber.Id, subscriptionRequest.Feed, subscriptionRequest.Topic, subscriptionRequest.IsAdd));
    }

    public void requestMonitor(Interactor monitor, MonitorRequest monitorRequest) {
        logger.info("Received monitor from {monitor} on \"{monitorRequest}\"");

        if (monitorRequest.IsAdd)
            _repository.addMonitor(monitor, monitorRequest.Feed);
        else
            _repository.removeMonitor(monitor, monitorRequest.Feed, false);
    }

    private void closeInteractor(Interactor interactor) {
        logger.fine("Removing subscriptions for {interactor}");

        // Remove the subscriptions
        List<FeedTopic> feedTopics = _repository.findFeedTopicsBySubscriber(interactor);
        for (var feedTopic : feedTopics) {
            _repository.removeSubscription(interactor, feedTopic.Feed, feedTopic.Topic, true);
        }

        var monitorEnumerator = Enumerable.create(feedTopics)
        		.select(new UnaryFunction<FeedTopic, String>() {
					@Override
					public String invoke(FeedTopic arg) {
						return arg.Feed;
					}
				})
        		.distinct(StringComparator.Default);
        
        for (var feed : monitorEnumerator) {
            _repository.removeMonitor(interactor, feed, true);
        }

        var subscriptionEnumerator = Enumerable.create(feedTopics)
        		.select(new UnaryFunction<FeedTopic, ForwardedSubscriptionRequest>() {
					@Override
					public ForwardedSubscriptionRequest invoke(FeedTopic feedTopic) {
						return new ForwardedSubscriptionRequest(interactor.Id, feedTopic.Feed, feedTopic.Topic, false);
					}
				});
        // Inform those interested that this interactor is no longer subscribed to these topics.
        for (var subscriptionRequest : subscriptionEnumerator) {
            _notificationManager.forwardSubscription(subscriptionRequest);
        }
    }

    public void sendUnicastData(Interactor publisher, UnicastData unicastData) {
        // Can we find this client in the subscribers to this topic?
        Interactor subscriber = Enumerable.create(_repository.GetSubscribersToFeedAndTopic(unicastData.Feed, unicastData.Topic))
                .firstOrDefault(new UnaryFunction<Interactor, Boolean>() {
					@Override
					public Boolean invoke(Interactor arg) {
						return arg.Id.equals(unicastData.ClientId);
					}
				});

        if (subscriber == null)
            return;

        _publisherManager.sendUnicastData(publisher, unicastData, subscriber);
    }

    public void sendMulticastData(Interactor publisher, MulticastData multicastData) {
    	var subscribers = _repository.GetSubscribersToFeedAndTopic(multicastData.Feed, multicastData.Topic);
        _publisherManager.sendMulticastData(publisher, subscribers, multicastData);
    }
}
