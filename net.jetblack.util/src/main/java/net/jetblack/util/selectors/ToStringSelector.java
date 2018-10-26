package net.jetblack.util.selectors;

import net.jetblack.util.invokables.UnaryFunction;

public class ToStringSelector<T> implements UnaryFunction<T, String> {

	@Override
	public String invoke(T arg) {
		return arg.toString();
	}

}