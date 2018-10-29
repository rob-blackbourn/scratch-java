package net.jetblack.util;

import java.util.Collection;
import java.util.Iterator;

public class Strings {

	public static String join(String separator, Iterator<?> iterator) {
		
		StringBuilder s = new StringBuilder();
		
		while (iterator.hasNext()) {
			if (s.length() > 0)
				s.append(separator);
			s.append(iterator.next());
		}
		
		return s.toString();
	}
	
	public static String join(String separator, Collection<?> collection) {
		return join(separator, collection.iterator());
	}
	
	public static String join(String separator, String[] array)
	{
		return join(separator, Enumerable.create(array));
	}
}