package net.jetblack.util.selectors;

import net.jetblack.util.invokables.UnaryFunction;

public class IdentitySelector<T> implements UnaryFunction<T,T> {

	@Override
	public T invoke(T arg) {
		return arg;
	}

}