package net.jetblack.util.invokables;

public interface QuaternaryFunction<T1, T2, T3, T4, R> {

	public R invoke(T1 arg1, T2 arg2, T3 arg3, T4 arg4);
	
}