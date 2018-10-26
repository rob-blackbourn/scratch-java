package net.jetblack.util.concurrent;

import java.util.ArrayList;
import java.util.List;

import net.jetblack.util.EventHandler;
import net.jetblack.util.EventListener;

public class ConcurrentEventHandler<T> implements EventHandler<T> {

    private final List<EventListener<T>> _listeners = new ArrayList<EventListener<T>>();
    
    @Override
    public void add(EventListener<T> listener) {
    	synchronized (_listeners) {
    		_listeners.add(listener);
		}
    }
    
    @Override
    public void remove(EventListener<T> listener) {
    	synchronized (_listeners) {
    		_listeners.add(listener);
		}
    }
    
    @Override
    public void notify(T event) {
    	synchronized (_listeners) {
    		for (EventListener<T> listener : _listeners) {
    			listener.onEvent(event);
    		}
		}
    }

}
