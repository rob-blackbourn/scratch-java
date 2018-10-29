package net.jetblack.util;

public class KeyValuePair<K, V> {

	public KeyValuePair(K key, V value) {
		Key = key;
		Value = value;
	}
	
	public final K Key;
	public final V Value;

	public static <K,V> KeyValuePair<K,V> create(K key, V value) {
		return new KeyValuePair<K,V>(key, value);
	}
}
