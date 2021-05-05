package dev.binclub.javaception.classloader;

import dev.binclub.javaception.*;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.runtime.ExecutionEngine;
import dev.binclub.javaception.type.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static dev.binclub.javaception.utils.GenericUtils.sneakyThrow;

/**
 * Stores the loaded classes
 */
public class SystemDictionary {
	private final VirtualMachine vm;
	private final Map<String, Set<Klass>> dictionary = new HashMap<>();
	
	public SystemDictionary(VirtualMachine vm) {
		this.vm = vm;
	}
	
	/**
	 * Attempts to find the class with the given name.
	 * If it is not found then it will be loaded/created.
	 *
	 * @param referencedBy The class that referenced this class through it's runtime constant pool
	 * @param className    The name of the referenced class
	 * @return The referenced class
	 */
	public Klass findReferencedClass(Klass referencedBy, Type className) {
		try {
			if (className instanceof ArrayType) {
				return vm.klassLoader.createArrayClass(referencedBy, referencedBy.classLoader, (ArrayType) className);
			} else if (className instanceof ClassType) {
				var classType = (ClassType) className;
				InstanceOop cl = null;
				if (referencedBy != null) {
					cl = referencedBy.classLoader;
				}
				return vm.klassLoader.loadClass(cl, classType.name);
			} else {
				throw new UnsupportedOperationException(className.getClass().getName());
			}
		} catch (ClassNotFoundException e) {
			sneakyThrow(e);
			throw new IllegalStateException("Unreachable");
		}
	}
	
	
	/// Well known classes
	
	private Klass java_lang_Object;
	public Klass java_lang_Object() {
		Klass klass = java_lang_Object;
		if (klass == null) {
			klass = findReferencedClass(null, Type.classType("java/lang/Object"));
			java_lang_Object = klass;
		}
		return klass;
	}
	
	private Klass java_lang_String;
	public Klass java_lang_String() {
		Klass klass = java_lang_String;
		if (klass == null) {
			klass = findReferencedClass(null, Type.classType("java/lang/String"));
			java_lang_String = klass;
		}
		return klass;
	}
	
	private Klass java_lang_Class;
	public Klass java_lang_Class(){
		Klass klass = java_lang_Class;
		if (klass == null) {
			klass = findReferencedClass(null, Type.classType("java/lang/Class"));
			java_lang_Class = klass;
		}
		return klass;
	}
}
