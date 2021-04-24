package dev.binclub.javaception.runtime.nativehooks;

import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.runtime.IHookedMethod;
import dev.binclub.javaception.type.ClassType;

public class ForName0 implements IHookedMethod {
	
	@Override
	public Object invokeMethodObj(Klass owner, InstanceOop instance, MethodInfo method, Object... args) {
		InstanceOop string = (InstanceOop) args[0];
		String name = SystemDictionary.OOPToString(string);
		return SystemDictionary.findReferencedClass(owner, ClassType.classType(name.replace(".","/")));
	}
}
