package net.jetblack.authfeedbus.distributor.roles;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import net.jetblack.util.Enumerable;

public class FeedRole {
	
    public FeedRole(String feed, Role allow, Role deny, boolean requiresEntitlement, List<InteractorRole> interactorRoles) {
        Feed = feed;
        Allow = allow;
        Deny = deny;
        RequiresEntitlement = requiresEntitlement;
        InteractorRoles = Enumerable.create(interactorRoles).toMap(x -> new InteractorRole.Key(x.Address, x.User), x -> x);
    }

    public final String Feed;
    public final Role Allow;
    public final Role Deny;
    public final boolean RequiresEntitlement;
    public Map<InteractorRole.Key, InteractorRole> InteractorRoles;

    public boolean hasRole(InetAddress address, String user, String feed, Role role, boolean decision) {
        if (Allow.hasFlag(role))
            decision = true;

        if (Deny.hasFlag(role))
            decision = false;

        InteractorRole interactorRole = InteractorRoles.get(new InteractorRole.Key(address, user));
        if (interactorRole != null)
            decision = interactorRole.hasRole(role, decision);

        return decision;
    }

    @Override
    public String toString() {
        return String.format(
        		"Feed=%s, Allow=%s, Deny=%s, RequiresEntitlement=%s, InteractorRoles=[%s]",
        		Feed, Allow, Deny, RequiresEntitlement, InteractorRoles);
    }
}
