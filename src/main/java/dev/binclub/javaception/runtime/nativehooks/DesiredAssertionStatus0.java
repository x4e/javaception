package dev.binclub.javaception.runtime.nativehooks;

import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.runtime.IHookedMethod;

public class DesiredAssertionStatus0 implements IHookedMethod {
	@Override
	public Object invokeMethodObj(Klass owner, InstanceOop instance, MethodInfo method, Object... args) {
		return 0; // false
	}
}
