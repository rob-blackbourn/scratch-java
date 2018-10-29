package net.jetblack.authfeedbus.distributor.interactors;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import net.jetblack.authfeedbus.distributor.roles.DistributorRole;
import net.jetblack.authfeedbus.distributor.roles.Role;
import net.jetblack.authfeedbus.messages.AuthorizationRequest;
import net.jetblack.authfeedbus.messages.AuthorizationResponse;
import net.jetblack.authfeedbus.messages.InteractorAdvertisement;
import net.jetblack.util.Disposable;
import net.jetblack.util.EventHandler;
import net.jetblack.util.EventRegister;
import net.jetblack.util.concurrent.ConcurrentEventHandler;

public class InteractorManager implements Disposable {

	private static final Logger logger = Logger.getLogger(InteractorManager.class.getName());

	private final InteractorRepository _repository;

	public InteractorManager(DistributorRole distributorRole) {
		_repository = new InteractorRepository(distributorRole);
	}

	private final EventHandler<AuthorizationResponseEventArg> _authorizationResponses = new ConcurrentEventHandler<AuthorizationResponseEventArg>();
	public final EventRegister<AuthorizationResponseEventArg> AuthorizationResponses = _authorizationResponses;

	private final EventHandler<InteractorClosedEventArgs> _closedInteractors = new ConcurrentEventHandler<InteractorClosedEventArgs>();
	public final EventRegister<InteractorClosedEventArgs> ClosedInteractors = _closedInteractors;

	public final EventHandler<InteractorFaultedEventArgs> _faultedInteractors = new ConcurrentEventHandler<InteractorFaultedEventArgs>();
	public final EventRegister<InteractorFaultedEventArgs> FaultedInteractors = _faultedInteractors;

	public void openInteractor(Interactor interactor) throws InterruptedException {
		logger.fine("Opening interactor " + interactor);

		var joinMessage = new InteractorAdvertisement(interactor.User, interactor.Address, true);
		var existingJoinMessages = new ArrayList<InteractorAdvertisement>();
		for (var existingInteractor : _repository.getInteractors()) {
			try {
				existingInteractor.sendMessage(joinMessage);
			} catch (InterruptedException error) {
				logger.log(Level.WARNING, "Failed to send join message", error);
			}
			existingJoinMessages
					.add(new InteractorAdvertisement(existingInteractor.User, existingInteractor.Address, true));
		}

		_repository.add(interactor);

		interactor.start();

		for (var message : existingJoinMessages) {
			interactor.sendMessage(message);
		}
	}

	public void closeInteractor(Interactor interactor) {
		logger.fine("Closing interactor " + interactor);
		removeInteractor(interactor);
		_closedInteractors.notify(new InteractorClosedEventArgs(interactor));
	}

	public void faultInteractor(Interactor interactor, Exception error) {
		logger.fine("Faulting interactor " + interactor);
		removeInteractor(interactor);
		_faultedInteractors.notify(new InteractorFaultedEventArgs(interactor, error));
	}

	public void advertiseInterator(Interactor interactor, InteractorAdvertisement message) {
		for (var existingInteractor : _repository.getInteractors())
			if (existingInteractor.Id != interactor.Id) {
				try {
					existingInteractor.sendMessage(message);
				} catch (InterruptedException error) {
					logger.log(Level.WARNING, "Failed to send message to " + existingInteractor, error);
				}
			}
	}

	private void removeInteractor(Interactor interactor) {
		_repository.remove(interactor);

		var message = new InteractorAdvertisement(interactor.User, interactor.Address, false);
		for (var existingInteractor : _repository.getInteractors()) {
			try {
				existingInteractor.sendMessage(message);
			} catch (InterruptedException error) {
				logger.log(Level.WARNING, "Failed to send message to " + existingInteractor, error);
			}
		}
	}

	public void requestAuthorisation(Interactor interactor, String feed, String topic) {
		logger.fine("Requesting authorisation Interactor=" + interactor + ", Feed=" + feed + ", Topic=" + topic);

		if (!isAuthorizationRequired(feed)) {
			logger.fine("No authorisation required");
			acceptAuthorization(interactor, new AuthorizationResponse(interactor.Id, feed, topic, false, null));
		} else {
			var authorizationRequest = new AuthorizationRequest(
					interactor.Id, interactor.Address, interactor.User, feed, topic
			);

			for (var authorizer : _repository.find(feed, Role.Authorize)) {
				try {
					logger.fine("Requesting authorization from " + authorizer);
					authorizer.sendMessage(authorizationRequest);
				} catch (Exception exception) {
					logger.log(
							Level.WARNING, "Failed to send " + authorizer + " message " + authorizationRequest,
							exception
					);
				}
			}
		}
	}

	public void acceptAuthorization(Interactor authorizer, AuthorizationResponse message) {
		logger.fine("Accepting an authorization response from " + authorizer + " with " + message);

		var requestor = _repository.find(message.ClientId);
		if (requestor == null) {
			logger.warning(
					"Unable to queue an authorization response for unknown ClientId=" + message.ClientId
							+ " for Feed=\"" + message.Feed + "\", Topic=\"" + message.Topic + "\"."
			);
			return;
		}

		_authorizationResponses.notify(new AuthorizationResponseEventArg(authorizer, requestor, message));
	}

	private boolean isAuthorizationRequired(String feed) {
		// Only specifically configured feeds require authroization.
		return _repository.DistributorRole.FeedRoles.containsKey(feed);
	}

	@Override
	public void dispose() {
		logger.fine("Disposing");
		_repository.dispose();
	}
}
