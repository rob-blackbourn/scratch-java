package net.jetblack.feedbus.distributor.notifiers;

import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorManager;
import net.jetblack.feedbus.messages.ForwardedSubscriptionRequest;
import net.jetblack.feedbus.messages.NotificationRequest;
import net.jetblack.util.EventHandler;
import net.jetblack.util.EventListener;
import net.jetblack.util.EventRegister;
import net.jetblack.util.concurrent.ConcurrentEventHandler;

public class NotificationManager {

	private static final Logger logger = Logger.getLogger(NotificationManager.class.getName());

    private final NotificationRepository _repository;
    private final EventHandler<NotificationEventArgs> _newNotificationRequest = new ConcurrentEventHandler<NotificationEventArgs>();
    
    public final EventRegister<NotificationEventArgs> NewNotificationRequest = _newNotificationRequest;

    public NotificationManager(InteractorManager interactorManager)
    {
        _repository = new NotificationRepository();
        
        interactorManager.InteractorClosed.add(new EventListener<InteractorClosedEventArgs>() {
			@Override
			public void onEvent(InteractorClosedEventArgs event) {
		        logger.fine("Removing notification requests from " + event.Interactor);
		        removeInteractor(event.Interactor);
			}
		});
        
        interactorManager.InteractorFaulted.add(new EventListener<InteractorFaultedEventArgs>() {
			
			@Override
			public void onEvent(InteractorFaultedEventArgs event) {
		        logger.fine("Interactor faulted: " + event.Interactor + " - " + event.Error);
		        removeInteractor(event.Interactor);
			}
		});
    }

    private void removeInteractor(Interactor interactor)
    {
        _repository.removeInteractor(interactor);
    }

    public void requestNotification(Interactor notifiable, NotificationRequest notificationRequest)
    {
        logger.info("Handling notification request for " + notifiable + " on " + notificationRequest);

        if (notificationRequest.IsAdd) {
            if (_repository.addRequest(notifiable, notificationRequest.Feed)) {
            	_newNotificationRequest.notify(new NotificationEventArgs(notifiable, notificationRequest.Feed));
            }
        }
        else
            _repository.removeRequest(notifiable, notificationRequest.Feed);
    }

    public void forwardSubscription(ForwardedSubscriptionRequest forwardedSubscriptionRequest)
    {
        // Find all the interactors that wish to be notified of subscriptions to this topic.
        var notifiables = _repository.findNotifiables(forwardedSubscriptionRequest.Feed);
        if (notifiables == null)
            return;

        logger.fine("Notifying interactors[" + notifiables + "] of subscription " + forwardedSubscriptionRequest);

        // Inform each notifiable interactor of the subscription request.
        for (var notifiable : notifiables) {
            try {
				notifiable.sendMessage(forwardedSubscriptionRequest);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

}
