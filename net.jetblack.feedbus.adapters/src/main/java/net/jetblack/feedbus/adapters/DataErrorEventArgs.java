package net.jetblack.feedbus.adapters;

public class DataErrorEventArgs {

    public DataErrorEventArgs(boolean isSending, String feed, String topic, boolean isImage, Object data, Exception error) {
        IsSending = isSending;
        Feed = feed;
        Topic = topic;
        IsImage = isImage;
        Data = data;
        Error = error;
    }

    public final boolean IsSending;
    public final String Feed;
    public final String Topic;
    public final boolean IsImage;
    public final Object Data;
    public final Exception Error;
}
