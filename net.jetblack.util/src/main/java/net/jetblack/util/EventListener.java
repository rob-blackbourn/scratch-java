package net.jetblack.util;

public interface EventListener<T> {
	void onEvent(T event);
}
