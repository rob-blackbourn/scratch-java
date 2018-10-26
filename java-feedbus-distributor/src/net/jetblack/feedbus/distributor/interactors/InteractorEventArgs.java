package net.jetblack.feedbus.distributor.interactors;

import net.jetblack.util.EventArgs;

public class InteractorEventArgs extends EventArgs {

    public InteractorEventArgs(Interactor interactor) {
        Interactor = interactor;
    }

    public final Interactor Interactor;
}
