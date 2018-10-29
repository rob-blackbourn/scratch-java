package net.jetblack.authfeedbus.distributor;

import java.util.Set;
import java.util.UUID;

public class AuthorizationInfo {

	public AuthorizationInfo(boolean isAuthorizationRequired, Set<UUID> entitlements) {
		_isAuthorizationRequired = isAuthorizationRequired;
		_entitlements = entitlements;
	}

	private boolean _isAuthorizationRequired;

	public boolean getIsAuthorizationRequired() {
		return _isAuthorizationRequired;
	}

	public void setIsAuthorizationRequired(boolean value) {
		_isAuthorizationRequired = value;
	}

	private Set<UUID> _entitlements;

	public Set<UUID> getEntitlements() {
		return _entitlements;
	}

	public void Entitlements(Set<UUID> value) {
		_entitlements = value;
	}
}
