package net.jetblack.feedbus.adapters;

import net.jetblack.util.EventArgs;

public class DataReceivedEventArgs extends EventArgs {

    public DataReceivedEventArgs(String feed, String topic, Object data, boolean isImage) {
        Feed = feed;
        Topic = topic;
        IsImage = isImage;
        Data = data;
    }

    public final String Feed;
    public final String Topic;
    public final boolean IsImage;
    public final Object Data;
}
