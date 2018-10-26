package net.jetblack.util;

import java.util.ArrayList;
import java.util.Collection;

public class TestNode {

	private final String value;
	private final Collection<TestNode> children = new ArrayList<TestNode>();
	
	public TestNode(String value) {
		this.value = value;
	}
	
	public TestNode add(String value) {
		TestNode node = new TestNode(value);
		children.add(node);
		return node;
	}
	
	public String getValue() {
		return value;
	}

	public Iterable<TestNode> getChildren() {
		return children;
	}
}
