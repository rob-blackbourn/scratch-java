package net.jetblack.feedbus.distributor.notifiers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jetblack.feedbus.distributor.interactors.Interactor;

public class NotificationRepository {

    private final Map<String, Set<Interactor>> _feedToNotifiables = new HashMap<String, Set<Interactor>>();

    public NotificationRepository() {
    }

    public void removeInteractor(Interactor interactor) {
        // Remove the interactor where it appears in the notifiables, remembering any topics which are left without any interactors.
        Set<String> topicsWithoutInteractors = new HashSet<String>();
        for (var topicPatternToNotifiable : _feedToNotifiables.entrySet())
        {
        	Set<Interactor> notifiables = topicPatternToNotifiable.getValue();
        	
        	if (!notifiables.contains(interactor)) {
        		continue;
        	}
        	
        	notifiables.remove(interactor);
            if (notifiables.isEmpty())
                topicsWithoutInteractors.add(topicPatternToNotifiable.getKey());
        }

        // Remove any topics left without interactors.
        for (String topic : topicsWithoutInteractors) {
            _feedToNotifiables.remove(topic);
        }
    }

    public boolean addRequest(Interactor notifiable, String feed)
    {
        // Find or create the set of notifiables for this feed.
        Set<Interactor> notifiables = _feedToNotifiables.get(feed);
        if (notifiables == null) {
            _feedToNotifiables.put(feed, notifiables = new HashSet<Interactor>());
        }
        else if (notifiables.contains(notifiable)) {
            return false;
        }

        // Add to the notifiables for this topic pattern and inform the subscription manager of the new notification request.
        notifiables.add(notifiable);
        return true;
    }

    public void removeRequest(Interactor notifiable, String feed) {
        // Does this feed have any notifiable interactors?
       Set<Interactor> notifiables = _feedToNotifiables.get(feed);
        if (notifiables == null) {
            return;
        }

        // Is this interactor in the set of notifiables for this feed?
        if (!notifiables.contains(notifiable)) {
            return;
        }

        // Remove the interactor from the set of notifiables.
        notifiables.remove(notifiable);

        // Are there any interactors left listening to this feed?
        if (!notifiables.isEmpty())
            return;

        // Remove the empty pattern from the caches.
        _feedToNotifiables.remove(feed);
    }

    public Set<Interactor> findNotifiables(String feed) {
        Set<Interactor> interactors = _feedToNotifiables.get(feed);
        return interactors == null ? null : interactors;
    }
}
