package net.jetblack.util;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class StringsTest {

	@Test
	public void JoinStringArrayTest() {
		var strings = new String[] { "one", "two", "three" };
		var joined = Strings.join(",", strings);
		assertEquals("one,two,three", joined);
	}

	@Test
	public void JoinStringCollectionTest() {
		var strings = new ArrayList<String>();
		strings.add("one");
		strings.add("two");
		strings.add("three");
		var joined = Strings.join(",", strings);
		assertEquals("one,two,three", joined);
	}

	@Test
	public void JoinStringEnumerableTest() {
		var strings = new ArrayList<String>();
		strings.add("one");
		strings.add("two");
		strings.add("three");
		var joined = Strings.join(",", Enumerable.create(strings));
		assertEquals("one,two,three", joined);
	}

}
