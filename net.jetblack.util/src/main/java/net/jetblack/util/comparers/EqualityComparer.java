package net.jetblack.util.comparers;

import net.jetblack.util.invokables.BinaryFunction;

public class EqualityComparer<T> implements BinaryFunction<T, T, Boolean> {

	@Override
	public Boolean invoke(T arg1, T arg2) {
		return (arg1 == null && arg2 == null) || (arg1 != null && arg1.equals(arg2));
	}

}