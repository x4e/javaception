package dev.binclub.javaception.klass;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classfile.FieldInfo;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.classloader.SystemDictionary;

public class ArrayKlass extends Klass {
	public final int dimensions;
	/**
	 * The type of the elements of this array. Must **not** be an {@link ArrayKlass}.
	 */
	public final Klass inner;
	
	public ArrayKlass(
		VirtualMachine vm,
		InstanceOop classLoader,
		Object[] runtimeConstantPool,
		String name,
		int dimensions,
		Klass inner
	) {
		super(vm, classLoader, runtimeConstantPool, name, vm.systemDictionary.java_lang_Object(), new Klass[0], new FieldInfo[0], new MethodInfo[0]);
		this.dimensions = dimensions;
		this.inner = inner;
	}
}
