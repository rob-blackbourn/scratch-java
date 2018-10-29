package net.jetblack.authfeedbus.distributor.interactors;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

import net.jetblack.authfeedbus.distributor.roles.DistributorRole;
import net.jetblack.authfeedbus.distributor.roles.Role;
import net.jetblack.authfeedbus.distributor.roles.RoleManager;
import net.jetblack.authfeedbus.messages.Message;
import net.jetblack.authfeedbus.messages.MessageInputStream;
import net.jetblack.authfeedbus.messages.MessageOutputStream;
import net.jetblack.util.Disposable;
import net.jetblack.util.concurrent.EventQueue;

public class Interactor implements Runnable, Disposable, Comparable<Interactor> {

	private static final Logger logger = Logger.getLogger(Interactor.class.getName());

    private final BlockingQueue<Message> _writeQueue;
    private final EventQueue<InteractorEventArgs> _eventQueue;
    private final Socket _socket;
    private final MessageInputStream _inputStream;
    private final MessageOutputStream _outputStream;
    private final AtomicBoolean _isCancelled;
    private final RoleManager _roleManager;
    private Thread _readThread;

    public static Interactor create(SSLSocket socket, DistributorRole distributorRole, EventQueue<InteractorEventArgs> eventQueue, int writeQueueCapacity, AtomicBoolean isCancelled) throws IOException
    {
        var interactor = new Interactor(socket, socket.getSession().getLocalPrincipal().getName(), distributorRole, eventQueue, writeQueueCapacity, isCancelled);
        return interactor;
    }

    private Interactor(Socket socket, String name, DistributorRole distributorRole, EventQueue<InteractorEventArgs> eventQueue, int writeQueueCapacity, AtomicBoolean isCancelled) throws IOException
    {
    	_socket = socket;
        _inputStream = new MessageInputStream(socket.getInputStream());
        _outputStream = new MessageOutputStream(socket.getOutputStream());
        _writeQueue = new ArrayBlockingQueue<Message>(writeQueueCapacity);
        Id = UUID.randomUUID();
        User = name;
        Address = socket.getInetAddress();
        _isCancelled = isCancelled;
        _eventQueue = eventQueue;
        _roleManager = new RoleManager(distributorRole, Address, name);
    }

    public final UUID Id;
    public final String User;
    public final InetAddress Address;

    public Thread start() {
    	Thread thread = new Thread(this);
    	return this.start();
    }
    
    public void run()
    {
    	_readThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				queueReceivedMessages();
			}
		});
    	
    	_readThread.start();
    	
    	writeQueuedMessages();
    	
    	try {
			_readThread.join();
		} catch (InterruptedException error) {
			// Nothing to do
		}
    }

    private void queueReceivedMessages()
    {
        while (!_isCancelled.get())
        {
            try
            {
                _eventQueue.enqueue(new InteractorMessageEventArgs(this, receiveMessage()));
            }
            catch (InterruptedException error)
            {
                break;
            }
            catch (Exception error)
            {
                try {
					_eventQueue.enqueue(new InteractorErrorEventArgs(this, error));
				} catch (InterruptedException internalError) {
					// Nothing  to do
				}
                break;
            }
        }

        logger.fine("Exited read loop for " + this);
    }

    private void writeQueuedMessages()
    {
        while (!_isCancelled.get())
        {
            try
            {
                var message = _writeQueue.take();
                message.write(_outputStream);
            }
            catch (InterruptedException error)
            {
                break;
            }
            catch (Exception error)
            {
                try {
					_eventQueue.enqueue(new InteractorErrorEventArgs(this, error));
				} catch (InterruptedException internalError) {
					// Nothing to do
				}
                break;
            }
        }

        logger.fine("Exited write loop for " + this);
    }

    public void sendMessage(Message message) throws InterruptedException
    {
        _writeQueue.put(message);
    }

    public Message receiveMessage() throws IOException
    {
        return Message.read(_inputStream);
    }

    public boolean hasRole(String feed, Role role)
    {
        return _roleManager.hasRole(feed, role);
    }

    public int compareTo(Interactor other)
    {
        return other == null ? 1 : Id.compareTo(other.Id);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Interactor && equals((Interactor)obj);
    }

    public boolean equals(Interactor other)
    {
        return other != null && other.Id == Id;
    }

    @Override
    public int hashCode()
    {
        return Id.hashCode();
    }

    @Override
    public String toString()
    {
        return Id + ":"  + User + "@" + Address;
    }

    @Override
    public void dispose()
    {
    	try {
			_socket.close();
		} catch (IOException error) {
			// Nothing to do
		}
    }
}
