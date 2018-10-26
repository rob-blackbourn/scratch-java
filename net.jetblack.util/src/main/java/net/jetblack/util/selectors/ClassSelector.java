package net.jetblack.util.selectors;

import net.jetblack.util.invokables.UnaryFunction;

public class ClassSelector<T extends Object> implements UnaryFunction<T, Class<?>> {

	@Override
	public Class<?> invoke(T arg) {
		return arg.getClass();
	}

}