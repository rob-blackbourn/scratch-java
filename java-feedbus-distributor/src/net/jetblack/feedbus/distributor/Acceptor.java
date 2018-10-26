package net.jetblack.feedbus.distributor;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorConnectedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorListener;
import net.jetblack.util.concurrent.EventQueue;

public class Acceptor implements Runnable {

	private static final Logger logger = Logger.getLogger(Acceptor.class.getName());

    private final EventQueue<InteractorEventArgs> _eventQueue;
    private final AtomicBoolean _isCancelled;
    public final InetAddress Address;
    public final int Port;

    public Acceptor(InetAddress address, int port, EventQueue<InteractorEventArgs> eventQueue, AtomicBoolean isCancelled) {
        _eventQueue = eventQueue;
        _isCancelled = isCancelled;
        Address = address;
        Port = port;
    }

    public Thread start() {
    	var thread = new Thread(this);
    	thread.start();
    	return thread;
    }
    
	@Override
	public void run() {
		InteractorListener listener;
		try {
			listener = new InteractorListener(Address, Port, _eventQueue, _isCancelled);
		} catch (IOException error) {
            logger.log(Level.SEVERE, "Failed to create server socket", error);
            return;
		}

        while (!_isCancelled.get())
        {
			try {
				Interactor interactor = listener.accept();
	            _eventQueue.enqueue(new InteractorConnectedEventArgs(interactor));
			} catch (InterruptedException error) {
	              logger.info("Thread interrupted - exiting");
	              break;
        	} catch (IOException error) {
	            logger.log(Level.SEVERE, "Failed to accept interactor", error);
	            break;
			}
        }
    }
}
