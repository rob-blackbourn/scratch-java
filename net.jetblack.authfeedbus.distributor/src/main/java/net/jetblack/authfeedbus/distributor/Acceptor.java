package net.jetblack.authfeedbus.distributor;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import net.jetblack.authfeedbus.distributor.interactors.Interactor;
import net.jetblack.authfeedbus.distributor.interactors.InteractorConnectedEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.authfeedbus.distributor.roles.DistributorRole;
import net.jetblack.util.concurrent.EventQueue;

public class Acceptor implements Runnable {

	private static final Logger logger = Logger.getLogger(Acceptor.class.getName());

    private final EventQueue<InteractorEventArgs> _eventQueue;
    private final AtomicBoolean _isCancelled;
    private final SSLServerSocket _serverSocket;
    private final DistributorRole _distributorRole;
    private final int _writeQueueCapacity;

    public Acceptor(InetAddress address, int port, DistributorRole distributorRole, EventQueue<InteractorEventArgs> eventQueue, int writeQueueCapacity, AtomicBoolean isCancelled)
    {
        _eventQueue = eventQueue;
        _isCancelled = isCancelled;
        _distributorRole = distributorRole;
        _writeQueueCapacity = writeQueueCapacity;
        _serverSocket = new SSLServerSocket(port, 10, address);
    }

    public void run()
    {
        while (!_isCancelled.get())
        {
            try
            {
                SSLSocket socket = (SSLSocket) _serverSocket.accept();
                var interactor = Interactor.create(socket, _distributorRole, _eventQueue, _writeQueueCapacity, _isCancelled);
                _eventQueue.enqueue(new InteractorConnectedEventArgs(interactor));
            }
            catch (Exception error)
            {
                logger.log(Level.WARNING, "Failed to accept interactor", error);
                break;
            }
        }
    }
}
