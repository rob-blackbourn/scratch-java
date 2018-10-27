package net.jetblack.authfeedbus.distributor.config;

import java.util.List;

import net.jetblack.authfeedbus.distributor.roles.FeedRole;
import net.jetblack.authfeedbus.distributor.roles.Role;
import net.jetblack.util.Enumerable;

public class FeedRoleConfig {
	private String _feed;

	public String getFeed() {
		return _feed;
	}

	public void setFeed(String value) {
		_feed = value;
	}

	private Role _allow;

	public Role getAllow() {
		return _allow;
	}

	public void setAllow(Role value) {
		_allow = value;
	}

	private Role _deny;

	public Role getDeny() {
		return _deny;
	}

	public void setDeny(Role value) {
		_deny = value;
	}

	private boolean _requiresEntitlement;

	public boolean getRequiresEntitlement() {
		return _requiresEntitlement;
	}

	public void setRequiresEntitlement(boolean value) {
		_requiresEntitlement = value;
	}

	private List<InteractorRoleConfig> _interactorRoles;

	public List<InteractorRoleConfig> getInteractorRoles() {
		return _interactorRoles;
	}

	public void setInteractorRoles(List<InteractorRoleConfig> value) {
		_interactorRoles = value;
	}

	public FeedRole toFeedRole() {
		return new FeedRole(_feed, _allow, _deny, _requiresEntitlement,
				Enumerable.create(_interactorRoles).select(x -> x.toInteractorRole()).toList());
	}
}
