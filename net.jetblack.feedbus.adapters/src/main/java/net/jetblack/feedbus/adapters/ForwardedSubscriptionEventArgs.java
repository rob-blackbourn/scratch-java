package net.jetblack.feedbus.adapters;

public class ForwardedSubscriptionEventArgs {

    public ForwardedSubscriptionEventArgs(String clientId, String feed, String topic, boolean isAdd) {
        ClientId = clientId;
        Feed = feed;
        Topic = topic;
        IsAdd = isAdd;
    }

    public final String ClientId;
    public final String Feed;
    public final String Topic;
    public final boolean IsAdd;
}
