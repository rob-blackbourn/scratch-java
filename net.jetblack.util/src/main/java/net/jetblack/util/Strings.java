package net.jetblack.util;

import java.util.Collection;
import java.util.Iterator;

public class Strings {

	public static String join(String separator, Iterator<String> iterator) {
		
		StringBuilder s = new StringBuilder();
		
		while (iterator.hasNext()) {
			if (s.length() > 0)
				s.append(separator);
			s.append(iterator.next());
		}
		
		return s.toString();
	}
	
	public static String join(String separator, Collection<String> collection) {
		return join(separator, collection.iterator());
	}
	
	public static String join(String separator, String[] array)
	{
		return join(separator, Enumerable.create(array));
	}
}