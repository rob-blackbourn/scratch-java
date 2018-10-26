package net.jetblack.feedbus.distributor;

import java.io.EOFException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import net.jetblack.feedbus.distributor.interactors.InteractorConnectedEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorErrorEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.feedbus.distributor.interactors.InteractorManager;
import net.jetblack.feedbus.distributor.interactors.InteractorMessageEventArgs;
import net.jetblack.feedbus.distributor.notifiers.NotificationManager;
import net.jetblack.feedbus.distributor.subscriptions.SubscriptionManager;
import net.jetblack.feedbus.messages.MonitorRequest;
import net.jetblack.feedbus.messages.MulticastData;
import net.jetblack.feedbus.messages.NotificationRequest;
import net.jetblack.feedbus.messages.SubscriptionRequest;
import net.jetblack.feedbus.messages.UnicastData;
import net.jetblack.util.Disposable;
import net.jetblack.util.EventListener;
import net.jetblack.util.concurrent.EventQueue;

public class Server implements Disposable {

	private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final AtomicBoolean _isCancelled = new AtomicBoolean();

    private final EventQueue<InteractorEventArgs> _eventQueue;
    private final Acceptor _acceptor;
    private final Timer _heartbeatTimer;

    private final InteractorManager _interactorManager;
    private final SubscriptionManager _subscriptionManager;
    private final NotificationManager _notificationManager;
    
    private Thread _eventQueueThread;
    private Thread _acceptThread;
    private List<Thread> _interactorThreads = new ArrayList<Thread>();

    public Server(InetAddress address, int port) {
        _eventQueue = new EventQueue<InteractorEventArgs>(_isCancelled);
        
        _eventQueue.addListener(new EventListener<InteractorEventArgs>() {
			@Override
			public void onEvent(Object sender, InteractorEventArgs event) {
				onInteractorEvent(sender, event);
			}
		});

        _heartbeatTimer = new Timer("Heartbeat", true);

        _acceptor = new Acceptor(address, port, _eventQueue, _isCancelled);

        _interactorManager = new InteractorManager();

        _notificationManager = new NotificationManager(_interactorManager);

        _subscriptionManager = new SubscriptionManager(_interactorManager, _notificationManager);
    }

    public void start(long heartbeatInterval) {
        logger.info("Starting server");

        _eventQueueThread = _eventQueue.start();
        _acceptThread = _acceptor.start();

        if (heartbeatInterval != 0)
            _heartbeatTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					sendHeartbeat();
				}
			}, heartbeatInterval, heartbeatInterval);

        logger.info("Server started");
    }

    private void onInteractorEvent(Object sender, InteractorEventArgs args) {
        if (args instanceof InteractorConnectedEventArgs)
            onInteractorConnected((InteractorConnectedEventArgs)args);
        else if (args instanceof InteractorMessageEventArgs)
            onMessage((InteractorMessageEventArgs)args);
        else if (args instanceof InteractorErrorEventArgs)
            onInteractorError((InteractorErrorEventArgs)args);
    }

    private void onInteractorConnected(InteractorConnectedEventArgs event) {
        _interactorManager.addInteractor(event.Interactor);
        var thread = event.Interactor.start();
        _interactorThreads.add(thread);
    }

    private void onInteractorError(InteractorErrorEventArgs event) {
        if (event.Error instanceof EOFException)
            _interactorManager.closeInteractor(event.Interactor);
        else
            _interactorManager.faultInteractor(event.Interactor, event.Error);
    }

    private void onMessage(InteractorMessageEventArgs event) {
        logger.fine(String.format("OnMessage(sender=%s, message=%s", event.Interactor, event.Message));

        switch (event.Message.Type) {
            case MonitorRequest:
                _subscriptionManager.requestMonitor(event.Interactor, (MonitorRequest)event.Message);
                break;

            case SubscriptionRequest:
                _subscriptionManager.requestSubscription(event.Interactor, (SubscriptionRequest)event.Message);
                break;

            case MulticastData:
                _subscriptionManager.sendMulticastData(event.Interactor, (MulticastData)event.Message);
                break;

            case UnicastData:
                _subscriptionManager.sendUnicastData(event.Interactor, (UnicastData)event.Message);
                break;

            case NotificationRequest:
                _notificationManager.requestNotification(event.Interactor, (NotificationRequest)event.Message);
                break;

            default:
                logger.warning(String.format("Received unknown message type %s from interactor %s.", event.Message.Type, event.Interactor));
                break;
        }
    }

    private void sendHeartbeat() {
        logger.fine("Sending heartbeat");
        try {
			_eventQueue.enqueue(new InteractorMessageEventArgs(null, new MulticastData("__admin__", "heartbeat", true, null)));
		} catch (InterruptedException e) {
			// TODO: Should this be caught?
		}
    }

    public void dispose() {
        logger.info("Stopping server");

        _heartbeatTimer.cancel();

        _isCancelled.set(true);
        
        for (var thread : _interactorThreads) {
        	try {
				thread.join();
			} catch (InterruptedException e) {
				// Nothing to do.
			}
        }
        
        try {
			_acceptThread.join();
		} catch (InterruptedException e) {
			// Nothing to do.
		}
        
        try {
			_eventQueueThread.join();
		} catch (InterruptedException e) {
			// Nothing to do.
		}

        logger.info("Server stopped");
    }
}
