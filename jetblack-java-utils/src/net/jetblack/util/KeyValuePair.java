package net.jetblack.util;

public class KeyValuePair<K, V> {

	public KeyValuePair(K key, V value) {
		Key = key;
		Value = value;
	}
	
	public final K Key;
	public final V Value;

}
