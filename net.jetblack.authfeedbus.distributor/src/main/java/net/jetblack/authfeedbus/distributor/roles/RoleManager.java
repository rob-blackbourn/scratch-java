package net.jetblack.authfeedbus.distributor.roles;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class RoleManager {

	private final DistributorRole _distributorRole;
	private final InetAddress _address;
	private final String _user;
	private final Map<String, Map<Role, Boolean>> _feedDecision = new HashMap<String, Map<Role, Boolean>>();

	public RoleManager(DistributorRole distributorPermission, InetAddress address, String user) {
		_distributorRole = distributorPermission;
		_address = address;
		_user = user;
	}

	public boolean hasRole(String feed, Role role) {
		// Check the cache .
		Map<Role, Boolean> roleDecision = _feedDecision.get(feed);
		if (roleDecision != null)
			_feedDecision.put(feed, roleDecision = new HashMap<Role, Boolean>());
		Boolean decision = roleDecision.get(role);
		if (decision != null)
			return decision;

		decision = _distributorRole.hasRole(_address, _user, feed, role);

		// Cache the decision;
		roleDecision.put(role, decision);

		return decision;
	}
}
