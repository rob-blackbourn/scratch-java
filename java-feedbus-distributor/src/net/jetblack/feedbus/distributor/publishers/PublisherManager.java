package net.jetblack.feedbus.distributor.publishers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorManager;
import net.jetblack.feedbus.messages.FeedTopic;
import net.jetblack.feedbus.messages.MulticastData;
import net.jetblack.feedbus.messages.UnicastData;
import net.jetblack.util.EventListener;

public class PublisherManager {


	private static final Logger logger = Logger.getLogger(PublisherManager.class.getName());

    private final PublisherRepository _repository;
    private final List<EventListener<StalePublisherEventArgs>> _stalePublisherListeners = new ArrayList<EventListener<StalePublisherEventArgs>>();

    public PublisherManager(InteractorManager interactorManager) {
        _repository = new PublisherRepository();
        
        interactorManager.addInteractorClosedListener(new EventListener<InteractorClosedEventArgs>() {
			@Override
			public void onEvent(Object sender, InteractorClosedEventArgs event) {
				closeInteractor(event.Interactor);
			}
		});
        
        interactorManager.addInteractorFaultedListener(new EventListener<InteractorFaultedEventArgs>() {
			
			@Override
			public void onEvent(Object sender, InteractorFaultedEventArgs event) {
		        logger.fine("Interactor faulted: " + event.Interactor + " - " + event.Error);
		        closeInteractor(event.Interactor);
			}
		});
    }

    public void addStalePublisherListener(EventListener<StalePublisherEventArgs> listener) {
    	synchronized (_stalePublisherListeners) {
    		_stalePublisherListeners.add(listener);
    	}
    }

    public void removeStalePublisherListener(EventListener<StalePublisherEventArgs> listener) {
    	synchronized (_stalePublisherListeners) {
    		_stalePublisherListeners.remove(listener);
    	}
    }
    
    private void notifyStalePublisherListeners(StalePublisherEventArgs event) {
    	synchronized (_stalePublisherListeners) {
    		for (EventListener<StalePublisherEventArgs> listener : _stalePublisherListeners) {
    			listener.onEvent(this, event);
    		}
    	}
    }

    // TODO: Change the order of the arguments
    public void sendUnicastData(Interactor publisher, UnicastData unicastData, Interactor subscriber) {
        _repository.addPublisher(publisher, unicastData.Feed, unicastData.Topic);
        try {
			subscriber.sendMessage(unicastData);
		} catch (InterruptedException error) {
			logger.log(Level.WARNING, String.format("Failed to send unicast data from %s to %s", publisher, subscriber), error);
		}
    }

    public void sendMulticastData(Interactor publisher, List<Interactor> subscribers, MulticastData multicastData) {
        for (Interactor subscriber : subscribers) {
			sendMulticastData(publisher, subscriber, multicastData);
        }
    }

    private void sendMulticastData(Interactor publisher, Interactor subscriber, MulticastData multicastData) {
        if (publisher != null)
            _repository.addPublisher(publisher, multicastData.Feed, multicastData.Topic);
        try {
			subscriber.sendMessage(multicastData);
		} catch (InterruptedException error) {
			logger.log(Level.WARNING, String.format("Failed to send multicast data from %s to %s", publisher, subscriber), error);
		}
    }

    private void closeInteractor(Interactor interactor) {
        Set<FeedTopic> topicsWithoutPublishers = _repository.removePublisher(interactor);
        if (topicsWithoutPublishers.size() > 0)
            notifyStalePublisherListeners(new StalePublisherEventArgs(interactor, topicsWithoutPublishers));
    }

}
