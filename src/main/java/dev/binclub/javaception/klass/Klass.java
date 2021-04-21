package dev.binclub.javaception.klass;

import dev.binclub.javaception.classfile.ClassFileConstants;
import dev.binclub.javaception.classfile.FieldInfo;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.runtime.ExecutionEngine;
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
	public final Object[] staticFields;
	public boolean resolved = false;
	int staticFieldCount;
	
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
		for (MethodInfo method : methods) {
			method.owner = this;
		}
		for (FieldInfo fieldInfo : fields) {
			if ((fieldInfo.access & ClassFileConstants.ACC_STATIC) != 0) {
				staticFieldCount++;
			}
		}
		staticFields = new Object[staticFieldCount];
	}
	
	public FieldInfo findField(String name, Type type) {
		for (FieldInfo field : this.fields) {
			if (name.equals(field.name) && type.equals(field.type)) {
				return field;
			}
		}
		return null;
	}
	
	public int getFieldID(String name, String descriptor) {
		for (int i = 0; i < fields.length; i++) {
			FieldInfo field = fields[i];
			if (name.equals(field.name) && descriptor.equals(field.descriptor)) {
				if ((field.access & ClassFileConstants.ACC_STATIC) != 0) {
					int offset = i - (fields.length - staticFieldCount);
					return offset;
				} else {
					return i;
				}
				
			}
		}
		throw new ClassFormatError("Could not find field id for " + name);
	}
	
	public MethodInfo findMethod(String name, Type[] descriptor) {
		for (MethodInfo method : this.methods) {
			if (name.equals(method.name) && Arrays.equals(descriptor, method.descriptor)) {
				return method;
			}
		}
		return null;
	}
	
	public int getMethodID(String name, String descriptor) {
		for (int i = 0; i < methods.length; i++) {
			MethodInfo method = methods[i];
			if (name.equals(method.name) && method.signature.equals(descriptor)) {
				return i;
			}
		}
		throw new ClassFormatError("Could not find method id for " + name);
	}
	
	public void resolve() {
		resolved = true;
		for (MethodInfo method : methods) {
			if (method.name.equals("<clinit>")) {
				ExecutionEngine.invokeMethodObj(this, null, method);
			}
		}
	}
	
	public InstanceOop newInstance() {
		return new InstanceOop(this, this.fields.length - this.staticFieldCount);
	}
	
	InstanceOop asKlass;
	static int nameId = -1;
	
	public InstanceOop getAsClass(){
		if (asKlass == null) {
			asKlass = SystemDictionary.java_lang_Class().newInstance();
			if(nameId == -1){
				nameId = SystemDictionary.java_lang_Class().getFieldID("name" , "Ljava/lang/String;");
				asKlass.fields[nameId] = name;
			}
		}
		return asKlass;
	}
}
