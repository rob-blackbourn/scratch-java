package net.jetblack.util.invokables;

public interface TernaryFunction<T1, T2, T3, R> {

	public R invoke(T1 arg1, T2 arg2, T3 arg3);
	
}