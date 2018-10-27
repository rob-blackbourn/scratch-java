package net.jetblack.authfeedbus.messages;

public enum MessageType {
	InteractorAdvertisement, MulticastData, UnicastData, ForwardedMulticastData, ForwardedUnicastData,
	ForwardedSubscriptionRequest, NotificationRequest, SubscriptionRequest, AuthorizationRequest, AuthorizationResponse
}
