package dev.binclub.javaception.klass;

import dev.binclub.javaception.classfile.FieldInfo;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.type.Type;

import java.util.Arrays;

public class Klass {
	/**
	 * The defining loader of this class.
	 * Not necessarily the same as the initiating loader.
	 * See JVMS 5.3.
	 */
	public final InstanceOop classLoader;
	public final String name;
	/**
	 * Super Class - only null if this class is java/lang/Object
	 */
	public final Klass superKlass;
	public final Klass[] interfaces;
	/**
	 * Valid constant types:
	 * - Class
	 * - FieldRef
	 * - MethodRef
	 * - InterfaceMethodRef
	 * - MethodHandle
	 * - MethodType
	 * - Dynamic
	 * - InvokeDynamic
	 * - String, int, long, float, double
	 */
	public final Object[] runtimeConstantPool;
	public final FieldInfo[] fields;
	public final MethodInfo[] methods;
	
	public Klass(
		InstanceOop classLoader,
		Object[] runtimeConstantPool,
		String name,
		Klass superKlass,
		Klass[] interfaces,
		FieldInfo[] fields,
		MethodInfo[] methods
	) {
		this.classLoader = classLoader;
		this.runtimeConstantPool = runtimeConstantPool;
		this.name = name;
		this.superKlass = superKlass;
		this.interfaces = interfaces;
		this.fields = fields;
		this.methods = methods;
	}
	
	public FieldInfo findField(String name, Type type) {
		for (FieldInfo field : this.fields) {
			if (name.equals(field.name) && type.equals(field.type)) {
				return field;
			}
		}
		return null;
	}
	
	public MethodInfo findMethod(String name, Type[] descriptor) {
		for (MethodInfo method : this.methods) {
			if (name.equals(method.name) && Arrays.equals(descriptor, method.descriptor)) {
				return method;
			}
		}
		return null;
	}
}
