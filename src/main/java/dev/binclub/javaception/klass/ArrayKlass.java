package dev.binclub.javaception.klass;

import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.runtime.SystemDictionary;

public class ArrayKlass extends Klass {
	private final int dimensions;
	private final Klass inner;
	
	public ArrayKlass(InstanceOop classLoader, RuntimeConstantPool cp, String name, int dimensions, Klass inner) {
		super(classLoader, cp, name, SystemDictionary.java_lang_Object());
		this.dimensions = dimensions;
		this.inner = inner;
	}
}
