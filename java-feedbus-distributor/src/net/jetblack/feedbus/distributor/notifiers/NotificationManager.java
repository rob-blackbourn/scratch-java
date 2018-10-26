package net.jetblack.feedbus.distributor.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorManager;
import net.jetblack.feedbus.messages.ForwardedSubscriptionRequest;
import net.jetblack.feedbus.messages.NotificationRequest;
import net.jetblack.util.Enumerable;
import net.jetblack.util.EventListener;
import net.jetblack.util.Strings;
import net.jetblack.util.invokables.UnaryFunction;

public class NotificationManager {

	private static final Logger logger = Logger.getLogger(NotificationManager.class.getName());

    private final NotificationRepository _repository;
    private final List<EventListener<NotificationEventArgs>> _newNotificationRequestListeners = new ArrayList<EventListener<NotificationEventArgs>>();

    public NotificationManager(InteractorManager interactorManager)
    {
        _repository = new NotificationRepository();
        
        interactorManager.addInteractorClosedListener(new EventListener<InteractorClosedEventArgs>() {
			@Override
			public void onEvent(Object source, InteractorClosedEventArgs event) {
		        logger.fine("Removing notification requests from " + event.Interactor);
		        removeInteractor(event.Interactor);
			}
		});
        
        interactorManager.addInteractorFaultedListener(new EventListener<InteractorFaultedEventArgs>() {
			
			@Override
			public void onEvent(Object source, InteractorFaultedEventArgs event) {
		        logger.fine("Interactor faulted: " + event.Interactor + " - " + event.Error);
		        removeInteractor(event.Interactor);
			}
		});
    }
    
    public void addNewNotificationRequestListener(EventListener<NotificationEventArgs> listener) {
    	synchronized (_newNotificationRequestListeners) {
			_newNotificationRequestListeners.add(listener);
		}
    }
    
    public void removeNewNotificationRequestListener(EventListener<NotificationEventArgs> listener) {
    	synchronized (_newNotificationRequestListeners) {
			_newNotificationRequestListeners.remove(listener);
		}
    }
    
    public void notifyNewNotificationRequestListeners(NotificationEventArgs event) {
    	synchronized (_newNotificationRequestListeners) {
    		for (EventListener<NotificationEventArgs> listener:_newNotificationRequestListeners) {
    			listener.onEvent(this, event);
    		}
		}
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
            	notifyNewNotificationRequestListeners(new NotificationEventArgs(notifiable, notificationRequest.Feed));
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
