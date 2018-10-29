package net.jetblack.authfeedbus.distributor.interactors;

import net.jetblack.util.EventArgs;

public class InteractorEventArgs extends EventArgs {

	public InteractorEventArgs(Interactor interactor) {
		Interactor = interactor;
	}

	public final Interactor Interactor;

	@Override
	public String toString() {
		return "Interactor=" + Interactor;
	}
}
