package net.jetblack.authfeedbus.distributor.notifiers;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetblack.authfeedbus.distributor.interactors.Interactor;
import net.jetblack.authfeedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorManager;
import net.jetblack.authfeedbus.messages.ForwardedSubscriptionRequest;
import net.jetblack.authfeedbus.messages.NotificationRequest;
import net.jetblack.authfeedbus.messages.SubscriptionRequest;
import net.jetblack.util.Enumerable;
import net.jetblack.util.EventHandler;
import net.jetblack.util.EventListener;
import net.jetblack.util.EventRegister;
import net.jetblack.util.Strings;
import net.jetblack.util.concurrent.ConcurrentEventHandler;

public class NotificationManager {

	private static final Logger logger = Logger.getLogger(NotificationManager.class.getName());

	private final NotificationRepository _repository;

	public NotificationManager(InteractorManager interactorManager) {
		_repository = new NotificationRepository();
		interactorManager.ClosedInteractors.add(new EventListener<InteractorClosedEventArgs>() {
			@Override
			public void onEvent(InteractorClosedEventArgs event) {
				onClosedInteractor(event);
			}
		});
		interactorManager.FaultedInteractors.add(new EventListener<InteractorFaultedEventArgs>() {
			@Override
			public void onEvent(InteractorFaultedEventArgs event) {
				onFaultedInteractor(event);
			}
		});
	}

	private final EventHandler<NotificationEventArgs> _newNotificationRequests = new ConcurrentEventHandler<NotificationEventArgs>();
	public final EventRegister<NotificationEventArgs> NewNotificationRequests = _newNotificationRequests;

	// TODO: Should this be private?
	public void onFaultedInteractor(InteractorFaultedEventArgs event) {
		logger.log(Level.FINE, "Interactor faulted: " + event.Interactor, event.Error);
		_repository.removeInteractor(event.Interactor);
	}

	// TODO: Should this be private?
	public void onClosedInteractor(InteractorClosedEventArgs event) {
		logger.fine("Removing notification requests from " + event.Interactor);
		_repository.removeInteractor(event.Interactor);
	}

	public void requestNotification(Interactor notifiable, NotificationRequest notificationRequest)
    {
        logger.fine("Handling notification request for " + notifiable + " on + " + notificationRequest);

        if (notificationRequest.IsAdd)
        {
            if (_repository.addRequest(notifiable, notificationRequest.Feed))
                _newNotificationRequests.notify(new NotificationEventArgs(notifiable, notificationRequest.Feed));
        }
        else
            _repository.removeRequest(notifiable, notificationRequest.Feed);
    }

	public void forwardSubscription(Interactor subscriber, SubscriptionRequest subscriptionRequest) {
		// Find all the interactors that wish to be notified of subscriptions to this
		// topic.
		var notifiables = _repository.findNotifiables(subscriptionRequest.Feed);
		if (notifiables == null)
			return;

		var forwardedSubscriptionRequest = new ForwardedSubscriptionRequest(
				subscriber.User, subscriber.Address, subscriber.Id, subscriptionRequest.Feed, subscriptionRequest.Topic,
				subscriptionRequest.IsAdd
		);

		logger.fine(
				"Notifying interactors[" + Strings.join(",", Enumerable.create(notifiables)) + "] of subscription " + forwardedSubscriptionRequest
		);

		// Inform each notifiable interactor of the subscription request.
		for (var notifiable : notifiables) {
			try {
				notifiable.sendMessage(forwardedSubscriptionRequest);
			} catch (Exception error) {
				logger.log(
						Level.FINE, "Failed to notify " + notifiable + " regarding " + forwardedSubscriptionRequest,
						error
				);
			}
		}
	}
}
