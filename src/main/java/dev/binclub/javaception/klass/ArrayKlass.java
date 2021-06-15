package dev.binclub.javaception.klass;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classfile.FieldInfo;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.classloader.SystemDictionary;

import static dev.binclub.javaception.classfile.ClassFileConstants.*;

public class ArrayKlass extends Klass {
	/**
	 * The depth of this array.
	 * 0 < dimensions < 256
	 */
	public final int dimensions;
	/**
	 * The type of the elements of this array. Must **not** be an {@link ArrayKlass}.
	 */
	public final Klass inner;
	
	// Arrays are always public final abstract
	private static final int ARRAY_ACCESS = ACC_PUBLIC | ACC_FINAL | ACC_ABSTRACT;
	
	public ArrayKlass(
		VirtualMachine vm,
		InstanceOop classLoader,
		Object[] runtimeConstantPool,
		String name,
		int dimensions,
		Klass inner
	) {
		super(
			vm,
			classLoader,
			runtimeConstantPool,
			name,
			vm.systemDictionary.java_lang_Object(),
			new Klass[0],
			ARRAY_ACCESS
		);
		if (dimensions <= 0 || dimensions > 255) {
			throw new IllegalArgumentException(
				"Dimension does not satisfy constraints: 0 < %d < 256"
					.formatted(dimensions)
			);
		}
		this.dimensions = dimensions;
		this.inner = inner;
	}
}
