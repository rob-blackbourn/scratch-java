package net.jetblack.feedbus.distributor.interactors;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import net.jetblack.feedbus.messages.Message;
import net.jetblack.util.concurrent.EventQueue;

public class Interactor implements Runnable, Comparable<Interactor>, Closeable {

	private static final Logger logger = Logger.getLogger(Interactor.class.getName());

	private final BlockingQueue<Message> _writeQueue = new ArrayBlockingQueue<Message>(Integer.MAX_VALUE);
	
	public final String Id;
	public final InetAddress Address;
	
	private final DataInputStream _inputStream;
	private final DataOutputStream _outputStream;
	private final AtomicBoolean _isCancelled;
	private final EventQueue<InteractorEventArgs> _eventQueue;
	
	private Thread _readThread, _writeThread;
	
	public static Interactor create(Socket socket, EventQueue<InteractorEventArgs> eventQueue, AtomicBoolean isCancelled) throws IOException {
		return new Interactor(
				new DataInputStream(socket.getInputStream()),
				new DataOutputStream(socket.getOutputStream()),
				socket.getInetAddress(),
				eventQueue,
				isCancelled);
	}
	
	public Interactor(DataInputStream inputStream, DataOutputStream outputStream, InetAddress address, EventQueue<InteractorEventArgs> eventQueue, AtomicBoolean isCancelled) {
		_inputStream = inputStream;
		_outputStream = outputStream;
		_isCancelled = isCancelled;
		Address = address;
		_eventQueue = eventQueue;
		Id = UUID.randomUUID().toString();
	}
	
    public Thread start() {
    	var thread = new Thread(this);
    	thread.start();
    	return thread;
    }
    
	@Override
	public void run() {
		_readThread = new Thread(new Runnable() {
			@Override
			public void run() {
				queueReceivedMessages();
			}
		});

		_writeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				writeQueuedMessages();
			}
		});
	}
	
	private void queueReceivedMessages() {
        while (!_isCancelled.get()) {
            try {
                _eventQueue.enqueue(new InteractorMessageEventArgs(this, receiveMessage()));
            }
            catch (InterruptedException error) {
                break;
            }
            catch (Exception error) {
                try {
					_eventQueue.enqueue(new InteractorErrorEventArgs(this, error));
				} catch (InterruptedException e) {
					// Nothing to do
				}
                break;
            }
        }

        logger.fine("Exited read loop for " + this);
	}
	
	private void writeQueuedMessages() {
        while (!_isCancelled.get()) {
            try {
                Message message = _writeQueue.take();
                message.write(_outputStream);
            }
            catch (InterruptedException error) {
                break;
            }
            catch (Exception error) {
                try {
					_eventQueue.enqueue(new InteractorErrorEventArgs(this, error));
				} catch (InterruptedException e) {
					// Nothing to do
				}
                break;
            }
        }

        logger.fine("Exited read loop for " + this);
	}

    public void sendMessage(Message message) throws InterruptedException {
        _writeQueue.put(message);
    }

    public Message receiveMessage() throws IOException {
        return Message.read(_inputStream);
    }
    
    @Override
    public boolean equals(Object obj) {
        return equals((Interactor)obj);
    }

    public boolean equals(Interactor other) {
        return other != null && other.Id == Id;
    }

    @Override
    public int hashCode() {
        return Id.hashCode();
    }

    @Override
    public String toString() {
        return Id + ":" + Address;
    }

    public void dispose() {
    }

	@Override
	public int compareTo(Interactor other) {
		return other == null ? 1 : Id.compareTo(other.Id);
	}
	
	public void join() throws InterruptedException {
		_readThread.join();
		_writeThread.join();
	}

	@Override
	public void close() throws IOException {
        _inputStream.close();
        _outputStream.close();
	}
}
