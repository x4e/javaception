package dev.binclub.javaception.utils;

public class GenericUtils {
	public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
		//noinspection unchecked
		throw (E) e;
	}
}
