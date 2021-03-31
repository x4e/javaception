package dev.binclub.javaception.runtime;

import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.classloader.KlassLoader;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.type.ArrayType;
import dev.binclub.javaception.type.ClassType;
import dev.binclub.javaception.type.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	public static Klass findReferencedClass(Klass referencedBy, ClassType className) throws ClassNotFoundException {
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
	}
	
	
	/// Well known classes
	
	public static Klass java_lang_Object() {
		return WellKnownClasses.java_lang_Object;
	}
	
	// Class to lazily initialize well known classes
	private static class WellKnownClasses {
		private static final Klass java_lang_Object;
		
		static {
			try {
				java_lang_Object = findReferencedClass(null, Type.classType("java/lang/Object"));
			}
			catch (ClassNotFoundException ex) {
				throw new Error(ex);
			}
		}
	}
}
