package net.jetblack.feedbus.distributor.interactors;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.jetblack.util.Disposable;
import net.jetblack.util.EventListener;

public class InteractorManager implements Disposable {

	private static final Logger logger = Logger.getLogger(InteractorManager.class.getName());

    private final InteractorRepository _repository;
    private final List<EventListener<InteractorClosedEventArgs>> _closedInteractorListeners = new ArrayList<EventListener<InteractorClosedEventArgs>>();
    private final List<EventListener<InteractorFaultedEventArgs>> _faultedInteractorListeners = new ArrayList<EventListener<InteractorFaultedEventArgs>>();

    public InteractorManager() {
        _repository = new InteractorRepository();
    }
    
    public void addInteractorClosedListener(EventListener<InteractorClosedEventArgs> listener) {
    	synchronized (_closedInteractorListeners) {
    		_closedInteractorListeners.add(listener);
    	}
    }
    
    public void removeInteractorClosedListener(EventListener<InteractorClosedEventArgs> listener) {
    	synchronized (_closedInteractorListeners) {
    		_closedInteractorListeners.remove(listener);
    	}
    }
    
    private void notifyInteractorClosedListeners(InteractorClosedEventArgs event) {
    	synchronized (_closedInteractorListeners) {
    		for (EventListener<InteractorClosedEventArgs> listener : _closedInteractorListeners) {
    			listener.onEvent(this, event);
    		}
    	}
    }
    
    public void addInteractorFaultedListener(EventListener<InteractorFaultedEventArgs> listener) {
    	synchronized (_faultedInteractorListeners) {
    		_faultedInteractorListeners.add(listener);
    	}
    }
    
    public void removeInteractorFaultedListener(EventListener<InteractorFaultedEventArgs> listener) {
    	synchronized (_faultedInteractorListeners) {
    		_faultedInteractorListeners.remove(listener);
    	}
    }
    
    private void notifyInteractorFaultedListeners(InteractorFaultedEventArgs event) {
    	synchronized (_faultedInteractorListeners) {
    		for (EventListener<InteractorFaultedEventArgs> listener : _faultedInteractorListeners) {
    			listener.onEvent(this, event);
    		}
    	}
    }

    public void addInteractor(Interactor interactor) {
        logger.info("Adding interactor: " + interactor);

        _repository.add(interactor);
    }

    public void closeInteractor(Interactor interactor) {
        logger.info("Closing interactor: " + interactor);

        _repository.remove(interactor);
        notifyInteractorClosedListeners(new InteractorClosedEventArgs(interactor));
    }

    public void faultInteractor(Interactor interactor, Exception error) {
        logger.info("Faulting interactor: " + interactor);

        _repository.remove(interactor);
        notifyInteractorFaultedListeners(new InteractorFaultedEventArgs(interactor, error));
    }

    @Override
    public void dispose() {
        logger.fine("Disposing all interactors.");
        try {
			_repository.dispose();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
