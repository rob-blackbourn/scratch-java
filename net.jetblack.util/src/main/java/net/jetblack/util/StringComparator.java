package net.jetblack.util;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		return o1.compareTo(o2);
	}
	
	public static Comparator<String> Default = new StringComparator();

}
