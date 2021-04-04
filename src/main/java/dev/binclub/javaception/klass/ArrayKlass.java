package dev.binclub.javaception.klass;

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
		InstanceOop classLoader,
		Object[] runtimeConstantPool,
		String name,
		int dimensions,
		Klass inner
	) {
		super(classLoader, runtimeConstantPool, name, SystemDictionary.java_lang_Object(), new FieldInfo[0], new MethodInfo[0]);
		this.dimensions = dimensions;
		this.inner = inner;
	}
}
