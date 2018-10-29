package net.jetblack.authfeedbus.distributor.interactors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.jetblack.authfeedbus.distributor.roles.DistributorRole;
import net.jetblack.authfeedbus.distributor.roles.Role;
import net.jetblack.util.Disposable;

public class InteractorRepository implements Disposable {

	private final Map<UUID, Interactor> _interactors = new HashMap<UUID, Interactor>();
	private final Map<String, Map<Role, Set<Interactor>>> _feedRoleInteractors = new HashMap<String, Map<Role, Set<Interactor>>>();

	public InteractorRepository(DistributorRole distributorRole) {
		DistributorRole = distributorRole;
	}

	public final DistributorRole DistributorRole;

	public void add(Interactor interactor) {
		_interactors.put(interactor.Id, interactor);
		addFeedRoles(interactor);
	}

	public void remove(Interactor interactor) {
		removeFeedRoles(interactor);
		_interactors.remove(interactor.Id);
	}

	public Interactor find(UUID id) {
		Interactor requestor = _interactors.get(id);
		if (requestor != null)
			return requestor;
		return null;
	}

	public Set<Interactor> find(String feed, Role role) {
		Map<Role, Set<Interactor>> roleInteractors = _feedRoleInteractors.get(feed);
		if (roleInteractors != null) {
			Set<Interactor> interactors = roleInteractors.get(role);
			if (interactors != null)
				return interactors;
		}

		return new HashSet<Interactor>();
	}

	private void addFeedRoles(Interactor interactor) {
		for (var feed : DistributorRole.FeedRoles.keySet()) {
			Map<Role, Set<Interactor>> roleInteractor = _feedRoleInteractors.get(feed);
			if (roleInteractor == null)
				_feedRoleInteractors.put(feed, roleInteractor = new HashMap<Role, Set<Interactor>>());

			for (var role : new Role[] { Role.Publish, Role.Subscribe, Role.Notify, Role.Authorize }) {
				if (DistributorRole.hasRole(interactor.Address, interactor.User, feed, role)) {
					Set<Interactor> interactors = roleInteractor.get(role);
					if (interactors == null)
						roleInteractor.put(role, interactors = new HashSet<Interactor>());

					interactors.add(interactor);
				}
			}
		}
	}

	private void removeFeedRoles(Interactor interactor) {
		var feedsModified = new HashSet<String>();

		for (var feedRoleInteractors : _feedRoleInteractors.entrySet()) {
			var rolesModified = new HashSet<Role>();

			for (var roleInteractors : feedRoleInteractors.getValue().entrySet()) {
				if (roleInteractors.getValue().remove(interactor))
					rolesModified.add(roleInteractors.getKey());
			}

			for (var role : rolesModified) {
				if (feedRoleInteractors.getValue().get(role).isEmpty()) {
					feedRoleInteractors.getValue().remove(role);
					feedsModified.add(feedRoleInteractors.getKey());
				}
			}
		}

		for (var feed : feedsModified) {
			if (_feedRoleInteractors.get(feed).isEmpty())
				_feedRoleInteractors.remove(feed);
		}
	}

	public void dispose() {
		for (var interactor : _interactors.values())
			interactor.dispose();
	}

    public Collection<Interactor> getInteractors()
    {
        return _interactors.values();
    }
}
