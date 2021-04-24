package dev.binclub.javaception.runtime;

import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;

public interface IHookedMethod {
	
	Object invokeMethodObj(Klass owner, InstanceOop instance, MethodInfo method, Object... args);
}
