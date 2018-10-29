package net.jetblack.authfeedbus.distributor.notifiers;

import net.jetblack.authfeedbus.distributor.interactors.Interactor;
import net.jetblack.authfeedbus.distributor.interactors.InteractorEventArgs;

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
