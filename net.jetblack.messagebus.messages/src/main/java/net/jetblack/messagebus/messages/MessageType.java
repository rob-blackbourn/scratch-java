package net.jetblack.messagebus.messages;

public enum MessageType {
    MulticastData,
    UnicastData,
    ForwardedSubscriptionRequest,
    NotificationRequest,
    SubscriptionRequest,
    MonitorRequest
}
