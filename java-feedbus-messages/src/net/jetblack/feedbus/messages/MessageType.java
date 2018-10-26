package net.jetblack.feedbus.messages;

public enum MessageType {
    MulticastData,
    UnicastData,
    ForwardedSubscriptionRequest,
    NotificationRequest,
    SubscriptionRequest,
    MonitorRequest
}
