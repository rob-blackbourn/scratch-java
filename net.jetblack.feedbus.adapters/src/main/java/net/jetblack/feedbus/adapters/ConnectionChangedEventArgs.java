package net.jetblack.feedbus.adapters;

import net.jetblack.util.EventArgs;

public class ConnectionChangedEventArgs extends EventArgs {

        public ConnectionChangedEventArgs(ConnectionState state, Exception error) {
            State = state;
            Error = error;
        }

        public final ConnectionState State;
        public final Exception Error;
}
