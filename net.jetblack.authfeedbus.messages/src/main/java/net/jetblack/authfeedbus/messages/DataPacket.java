package net.jetblack.authfeedbus.messages;

import java.util.UUID;

public class DataPacket {

    public DataPacket(UUID header, Object body) {
        Header = header;
        Body = body;
    }

    public final UUID Header;
    public final Object Body;
}
