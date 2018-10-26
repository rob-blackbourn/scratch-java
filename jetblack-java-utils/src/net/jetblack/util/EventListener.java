package net.jetblack.util;

public interface EventListener<T> {
	void onEvent(Object source, T event);
}
