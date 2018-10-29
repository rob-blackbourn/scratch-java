package net.jetblack.authfeedbus.distributor;

import java.io.EOFException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import net.jetblack.authfeedbus.distributor.interactors.InteractorConnectedEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorErrorEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorManager;
import net.jetblack.authfeedbus.distributor.interactors.InteractorMessageEventArgs;
import net.jetblack.authfeedbus.distributor.notifiers.NotificationManager;
import net.jetblack.authfeedbus.distributor.roles.DistributorRole;
import net.jetblack.authfeedbus.distributor.subscribers.SubscriptionManager;
import net.jetblack.authfeedbus.messages.AuthorizationResponse;
import net.jetblack.authfeedbus.messages.MulticastData;
import net.jetblack.authfeedbus.messages.NotificationRequest;
import net.jetblack.authfeedbus.messages.SubscriptionRequest;
import net.jetblack.authfeedbus.messages.UnicastData;
import net.jetblack.util.Disposable;
import net.jetblack.util.concurrent.EventQueue;

public class Server implements Runnable, Disposable {

	private static final Logger logger = Logger.getLogger(Server.class.getName());

	private final AtomicBoolean _isCancelled = new AtomicBoolean();
	private final EventQueue<InteractorEventArgs> _eventQueue;
	private final Acceptor _acceptor;
	private final Timer _heartbeatTimer;
	private final int _heartbeatInterval;

	private final InteractorManager _interactorManager;
	private final SubscriptionManager _subscriptionManager;
	private final NotificationManager _notificationManager;

	public Server(
			InetAddress address, int port, DistributorRole distributorRole, int heartbeatInterval,
			int interactorWriteQueueCapacity, int serverEventQueueCapacity
	) {
		DistributorRole = distributorRole;

		_eventQueue = new EventQueue<InteractorEventArgs>(_isCancelled, serverEventQueueCapacity);
		_eventQueue.Listener.add(args -> onInteractorEvent(args));

		_heartbeatTimer = new Timer();
		_heartbeatInterval = heartbeatInterval;

		_acceptor = new Acceptor(
				address, port, distributorRole, _eventQueue, interactorWriteQueueCapacity, _isCancelled
		);

		_interactorManager = new InteractorManager(distributorRole);

		_notificationManager = new NotificationManager(_interactorManager);

		_subscriptionManager = new SubscriptionManager(_interactorManager, _notificationManager);
	}

	public final DistributorRole DistributorRole;

	public void run() {
		logger.info("Starting server");

		_eventQueue.start();

		if (_heartbeatInterval != 0)
			_heartbeatTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

				}
			}, _heartbeatInterval, _heartbeatInterval);

		_acceptor.run();

		logger.info("Server started");
	}

	private void onInteractorEvent(InteractorEventArgs args) {
		if (args instanceof InteractorConnectedEventArgs)
			onInteractorConnected((InteractorConnectedEventArgs) args);
		else if (args instanceof InteractorMessageEventArgs)
			onMessage((InteractorMessageEventArgs) args);
		else if (args instanceof InteractorErrorEventArgs)
			onInteractorError((InteractorErrorEventArgs) args);
	}

	private void onInteractorConnected(InteractorConnectedEventArgs args) {
		try {
			_interactorManager.openInteractor(args.Interactor);
		} catch (InterruptedException error) {
			// TODO Auto-generated catch block
			error.printStackTrace();
		}
	}

	private void onInteractorError(InteractorErrorEventArgs args) {
		if (args.Error instanceof EOFException)
			_interactorManager.closeInteractor(args.Interactor);
		else
			_interactorManager.faultInteractor(args.Interactor, args.Error);
	}

	private void onMessage(InteractorMessageEventArgs args) {
		logger.fine("OnMessage(sender=" + args.Interactor + ", message=" + args.Message);

		switch (args.Message.Type) {
		case AuthorizationResponse:
			_interactorManager.acceptAuthorization(args.Interactor, (AuthorizationResponse) args.Message);
			break;

		case SubscriptionRequest:
			_subscriptionManager.requestSubscription(args.Interactor, (SubscriptionRequest) args.Message);
			break;

		case MulticastData:
			_subscriptionManager.sendMulticastData(args.Interactor, (MulticastData) args.Message);
			break;

		case UnicastData:
			_subscriptionManager.sendUnicastData(args.Interactor, (UnicastData) args.Message);
			break;

		case NotificationRequest:
			_notificationManager.requestNotification(args.Interactor, (NotificationRequest) args.Message);
			break;

		default:
			logger.warning(
					"Received unknown message type " + args.Message.Type + " from interactor " + args.Interactor
			);
			break;
		}
	}

	private void heartbeatCallback() {
		logger.fine("Sending heartbeat");
		try {
			_eventQueue
					.enqueue(new InteractorMessageEventArgs(null, new MulticastData("__admin__", "heartbeat", true, null)));
		} catch (InterruptedException error) {
			// TODO Auto-generated catch block
			error.printStackTrace();
		}
	}

	public void dispose() {
		logger.info("Stopping server");

		_heartbeatTimer.cancel();

		_isCancelled.set(true);

		logger.info("Server stopped");
	}
}
