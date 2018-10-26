package net.jetblack.util.comparers;

import net.jetblack.util.invokables.UnaryFunction;

public class EqualsValue<T> implements UnaryFunction<T,Boolean> {

	private final T value;
	
	public EqualsValue(T value) {
		this.value = value;
	}
	
	@Override
	public Boolean invoke(T arg) {
		return (value == null && arg == null) || (value != null && value.equals(arg));
	}

}