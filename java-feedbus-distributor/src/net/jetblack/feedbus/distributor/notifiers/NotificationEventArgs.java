package net.jetblack.feedbus.distributor.notifiers;

import net.jetblack.feedbus.distributor.interactors.Interactor;
import net.jetblack.feedbus.distributor.interactors.InteractorEventArgs;

public class NotificationEventArgs extends InteractorEventArgs {

    public NotificationEventArgs(Interactor interactor, String feed) {
        super(interactor);
        Feed = feed;
    }

    public final String Feed;

	@Override
	public String toString() {
	    return super.toString() + ", Feed=" + Feed;
	}
}
