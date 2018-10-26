package net.jetblack.util.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import net.jetblack.util.EventListener;

import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class EventQueue<T> implements Runnable {
	
	private static final Logger logger = Logger.getLogger(EventQueue.class.getName());


    private final AtomicBoolean _isCancelled;
    private final BlockingQueue<T> _interactorEventQueue = new ArrayBlockingQueue<T>(Integer.MAX_VALUE);
    private final List<EventListener<T>> _eventListeners = new ArrayList<EventListener<T>>(); 

    public EventQueue(AtomicBoolean isCancelled) {
    	_isCancelled = isCancelled;
    }

    public void addListener(EventListener<T> listener) {
    	synchronized(_eventListeners) {
    		_eventListeners.add(listener);
    	}
    }

    public void removeListener(EventListener<T> listener) {
    	synchronized(_eventListeners) {
    		_eventListeners.remove(listener);
    	}
    }
    
    private void notifyListeners(T event) {
    	synchronized(_eventListeners) {
	    	for (EventListener<T> listener : _eventListeners) {
	    		listener.onEvent(this, event);
	    	}
    	}
    }

    public void enqueue(T item) throws InterruptedException {
        _interactorEventQueue.put(item);
    }

    public Thread start() {
    	var thread = new Thread(this);
    	thread.start();
    	return thread;
    }
    
	@Override
	public void run() {
        while (!_isCancelled.get()) {
            try {
                T item = _interactorEventQueue.take();
                notifyListeners(item);
            }
            catch (Exception error) {
                logger.severe("The event queue has faulted");
                break;
            }
        }

        logger.info("Exited the event loop");
	}
}
