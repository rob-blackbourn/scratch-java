package net.jetblack.feedbus.distributor.interactors;

import java.util.logging.Logger;

import net.jetblack.util.Disposable;
import net.jetblack.util.EventHandler;
import net.jetblack.util.EventRegister;
import net.jetblack.util.concurrent.ConcurrentEventHandler;

public class InteractorManager implements Disposable {

	private static final Logger logger = Logger.getLogger(InteractorManager.class.getName());

    private final InteractorRepository _repository;
    private final EventHandler<InteractorClosedEventArgs> _interactorClosed = new ConcurrentEventHandler<InteractorClosedEventArgs>();
    private final EventHandler<InteractorFaultedEventArgs> _interactorFaulted = new ConcurrentEventHandler<InteractorFaultedEventArgs>();

    public final EventRegister<InteractorClosedEventArgs> InteractorClosed = _interactorClosed;
    public final EventRegister<InteractorFaultedEventArgs> InteractorFaulted = _interactorFaulted;

    public InteractorManager() {
        _repository = new InteractorRepository();
    }

    public void addInteractor(Interactor interactor) {
        logger.info("Adding interactor: " + interactor);

        _repository.add(interactor);
    }

    public void closeInteractor(Interactor interactor) {
        logger.info("Closing interactor: " + interactor);

        _repository.remove(interactor);
        _interactorClosed.notify(new InteractorClosedEventArgs(interactor));
    }

    public void faultInteractor(Interactor interactor, Exception error) {
        logger.info("Faulting interactor: " + interactor);

        _repository.remove(interactor);
        _interactorFaulted.notify(new InteractorFaultedEventArgs(interactor, error));
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
