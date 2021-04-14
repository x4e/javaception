package dev.binclub.javaception.classloader;

import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.classloader.KlassLoader;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.type.ArrayType;
import dev.binclub.javaception.type.ClassType;
import dev.binclub.javaception.type.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static dev.binclub.javaception.utils.GenericUtils.sneakyThrow;

/**
 * Stores the loaded classes
 */
public class SystemDictionary {
	private static final Map<String, Set<Klass>> dictionary = new HashMap<>();
	
	/**
	 * Attempts to find the class with the given name.
	 * If it is not found then it will be loaded/created.
	 *
	 * @param referencedBy The class that referenced this class through it's runtime constant pool
	 * @param className    The name of the referenced class
	 * @return The referenced class
	 */
	public static Klass findReferencedClass(Klass referencedBy, ClassType className) {
		try {
			if (className instanceof ArrayType) {
				return KlassLoader.createArrayClass(referencedBy, referencedBy.classLoader, (ArrayType) className);
			}
			else {
				InstanceOop cl = null;
				if (referencedBy != null) {
					cl = referencedBy.classLoader;
				}
				return KlassLoader.loadClass(cl, className.name);
			}
		} catch (ClassNotFoundException e) {
			sneakyThrow(e);
			throw new IllegalStateException("Unreachable");
		}
	}
	
	
	/// Well known classes
	
	private static Klass java_lang_Object;
	public static Klass java_lang_Object() {
		Klass klass = java_lang_Object;
		if (klass == null) {
			klass = findReferencedClass(null, Type.classType("java/lang/Object"));
			java_lang_Object = klass;
		}
		return klass;
	}
	
	private static Klass java_lang_String;
	public static Klass java_lang_String() {
		Klass klass = java_lang_String;
		if (klass == null) {
			klass = findReferencedClass(null, Type.classType("java/lang/String"));
			java_lang_String = klass;
		}
		return klass;
	}
	
	public static InstanceOop createEmptyStringInstance(){
		 Klass stringKlass = SystemDictionary.java_lang_String();
		 int valID = stringKlass.getFieldID("value", "[B");
		 int coderID = stringKlass.getFieldID("coder", "B");
		 InstanceOop instance = stringKlass.newInstance();
		 instance.fields[valID] = new byte[]{};
		 instance.fields[coderID] = 0;
		 return instance; 
	}
}
