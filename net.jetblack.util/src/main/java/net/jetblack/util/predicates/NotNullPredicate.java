package net.jetblack.util.predicates;

import net.jetblack.util.invokables.UnaryFunction;

public class NotNullPredicate<T> implements UnaryFunction<T,Boolean> {

	@Override
	public Boolean invoke(T arg) {
		return arg != null;
	}

}