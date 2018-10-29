package net.jetblack.authfeedbus.distributor.publishers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetblack.authfeedbus.distributor.AuthorizationInfo;
import net.jetblack.authfeedbus.distributor.interactors.Interactor;
import net.jetblack.authfeedbus.distributor.interactors.InteractorClosedEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorFaultedEventArgs;
import net.jetblack.authfeedbus.distributor.interactors.InteractorManager;
import net.jetblack.authfeedbus.distributor.roles.Role;
import net.jetblack.authfeedbus.messages.BinaryDataPacket;
import net.jetblack.authfeedbus.messages.ForwardedMulticastData;
import net.jetblack.authfeedbus.messages.ForwardedUnicastData;
import net.jetblack.authfeedbus.messages.MulticastData;
import net.jetblack.authfeedbus.messages.UnicastData;
import net.jetblack.util.Enumerable;
import net.jetblack.util.EventHandler;
import net.jetblack.util.EventListener;
import net.jetblack.util.EventRegister;
import net.jetblack.util.concurrent.ConcurrentEventHandler;

public class PublisherManager {

	private static final Logger logger = Logger.getLogger(PublisherManager.class.getName());

	private final PublisherRepository _repository;

	public PublisherManager(InteractorManager interactorManager) {
		_repository = new PublisherRepository();
		interactorManager.ClosedInteractors.add(new EventListener<InteractorClosedEventArgs>() {

			@Override
			public void onEvent(InteractorClosedEventArgs event) {
				onClosedInteractor(event);
			}
		});

		interactorManager.FaultedInteractors.add(new EventListener<InteractorFaultedEventArgs>() {

			@Override
			public void onEvent(InteractorFaultedEventArgs event) {
				// TODO Auto-generated method stub
				onFaultedInteractor(event);
			}
		});
	}

	private EventHandler<StalePublisherEventArgs> _stalePublishers = new ConcurrentEventHandler<StalePublisherEventArgs>();
	public EventRegister<StalePublisherEventArgs> StalePublishers = _stalePublishers;

	public void sendUnicastData(
			Interactor publisher, Interactor subscriber, AuthorizationInfo authorization, UnicastData unicastData
	) {
		if (!publisher.hasRole(unicastData.Feed, Role.Publish)) {
			logger.warning("Rejected request from " + publisher + " to publish on feed " + unicastData.Feed);
			return;
		}

		var clientUnicastData = authorization
				.getIsAuthorizationRequired()
						? new ForwardedUnicastData(
								publisher.User, publisher.Address, unicastData.ClientId, unicastData.Feed,
								unicastData.Topic, unicastData.IsImage, (BinaryDataPacket[]) Enumerable.create(
										unicastData.Data
								).where(x -> authorization.getEntitlements().contains(x.Header)).toList().toArray()
						)
						: new ForwardedUnicastData(
								publisher.User, publisher.Address, unicastData.ClientId, unicastData.Feed,
								unicastData.Topic, unicastData.IsImage, unicastData.Data
						);

		logger.fine("Sending unicast data from " + publisher + " to " + subscriber + ": " + clientUnicastData);

		_repository.addPublisher(publisher, clientUnicastData.Feed, clientUnicastData.Topic);

		try {
			subscriber.sendMessage(clientUnicastData);
		} catch (Exception exception) {
			logger.log(
					Level.FINE, "Failed to send to subscriber " + subscriber + " unicast data " + clientUnicastData,
					exception
			);
		}
	}

	public void sendMulticastData(
			Interactor publisher, Collection<Map.Entry<Interactor, AuthorizationInfo>> subscribers, MulticastData multicastData
	) {
		if (!(publisher == null || publisher.hasRole(multicastData.Feed, Role.Publish))) {
			logger.warning("Rejected request from " + publisher + " to publish to Feed " + multicastData.Feed);
			return;
		}

		for (var subscriberAndAuthorizationInfo : subscribers) {
			var subscriber = subscriberAndAuthorizationInfo.getKey();
			var authorizationInfo = subscriberAndAuthorizationInfo.getValue();

			var subscriberMulticastData = subscriberAndAuthorizationInfo.getValue().getIsAuthorizationRequired()
					? new ForwardedMulticastData(
							publisher.User, publisher.Address, multicastData.Feed, multicastData.Topic,
							multicastData.IsImage,
							filterDataPackets(authorizationInfo.getEntitlements(), multicastData.Data)
					)
					: new ForwardedMulticastData(
							publisher.User, publisher.Address, multicastData.Feed, multicastData.Topic,
							multicastData.IsImage, multicastData.Data
					);

			logger.fine(
					"Sending multicast data from " + publisher + " to " + subscriber + ": " + subscriberMulticastData
			);

			if (publisher != null)
				_repository.addPublisher(publisher, subscriberMulticastData.Feed, subscriberMulticastData.Topic);

			try {
				subscriber.sendMessage(subscriberMulticastData);
			} catch (Exception error) {
				logger.log(
						Level.FINE,
						"Failed to send to subscriber " + subscriber + " multicast data " + subscriberMulticastData,
						error
				);
			}
		}
	}

	private BinaryDataPacket[] filterDataPackets(Set<UUID> authorizations, BinaryDataPacket[] data) {
		// TODO: Optimise
		return (BinaryDataPacket[]) Enumerable.create(data).where(x -> authorizations.contains(x.Header)).toList()
				.toArray();
	}

	public void onClosedInteractor(InteractorClosedEventArgs args) {
		closeInteractor(args.Interactor);
	}

	public void onFaultedInteractor(InteractorFaultedEventArgs args) {
		logger.log(Level.FINE, "Interactor faulted: " + args.Interactor, args.Error);
		closeInteractor(args.Interactor);
	}

	private void closeInteractor(Interactor interactor) {
		var topicsWithoutPublishers = _repository.RemovePublisher(interactor);
		if (topicsWithoutPublishers != null)
			_stalePublishers.notify(
					new StalePublisherEventArgs(interactor, Enumerable.create(topicsWithoutPublishers).toList())
			);
	}
}
