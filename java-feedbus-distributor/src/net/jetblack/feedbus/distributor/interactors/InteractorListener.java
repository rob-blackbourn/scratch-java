package net.jetblack.feedbus.distributor.interactors;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

import net.jetblack.util.Disposable;
import net.jetblack.util.concurrent.EventQueue;

public class InteractorListener implements Disposable {

    private final EventQueue<InteractorEventArgs> _eventQueue;
    private final AtomicBoolean _isCancelled;
    private final ServerSocket _listener;

    public InteractorListener(InetAddress address, int port, EventQueue<InteractorEventArgs> eventQueue, AtomicBoolean isCancelled) throws IOException {
        _eventQueue = eventQueue;
        _isCancelled = isCancelled;
    	_listener = new ServerSocket(port, -1, address);
    }
    
    public Interactor accept() throws IOException {
        var socket = _listener.accept();
        return Interactor.create(socket, _eventQueue, _isCancelled);
    }

    @Override
    public void dispose() {
        try {
			_listener.close();
		} catch (IOException e) {
			// Nothing to do
		}
    }
}
