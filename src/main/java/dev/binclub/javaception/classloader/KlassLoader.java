package dev.binclub.javaception.classloader;

import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.klass.ArrayKlass;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.type.ArrayType;
import dev.binclub.javaception.type.ClassType;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to lookup classes, typically from a file system
 */
public class KlassLoader {
	/**
	 * Map of initiating classloader to created classes
	 */
	private static final Map<InstanceOop, Map<String, Klass>> classCache = new HashMap<>();
	
	private static Map<String, Klass> getCache(InstanceOop classLoader) {
		return classCache.computeIfAbsent(classLoader, k -> new HashMap<>());
	}
	
	/**
	 * Create the array class denoted by arrayType
	 *
	 * @param referencedBy The class that referenced this arrayType, may be null
	 * @param classLoader  The classloader of referencedBy
	 * @param arrayType    The type of the array to create
	 * @return A class representing this arrayType
	 * @throws ClassNotFoundException The array could not be created because the inner class was not found
	 */
	public static Klass createArrayClass(Klass referencedBy, InstanceOop classLoader, ArrayType arrayType) {
		String name = arrayType.name;
		
		Map<String, Klass> cache = getCache(classLoader);
		Klass cached = cache.get(name);
		if (cached != null) {
			return cached;
		}
		
		Klass out;
		if (arrayType.inner instanceof ClassType) {
			Klass inner = SystemDictionary.findReferencedClass(referencedBy, (ClassType) arrayType.inner);
			out = new ArrayKlass(classLoader, null, name, arrayType.dimensions, inner);
		} else {
			// TODO: primitive array support
			throw new UnsupportedOperationException();
		}
		cache.put(name, out);
		return out;
	}
	
	public static Klass loadClass(InstanceOop classLoader, String name) throws ClassNotFoundException {
		if (classLoader == null) {
			// If referencedBy was defined by the boostrap class loader, then the bootstrap class loader initiates
			// loading of the class denoted by className
			
			// TODO: bootstrap class loading logic
			Klass out = BootstrapKlassLoader.loadClass(name);
			if (out != null) {
				return out;
			} else {
				try {
					return ClassFileParser.parse(KlassLoader.class.getClassLoader()
						                      .getResourceAsStream(name + ".class"), null);
				}
				catch (Throwable err) {
				
				}
			}
			throw new ClassNotFoundException(name);
		} else {
			//noinspection SynchronizationOnLocalVariableOrMethodParameter
			synchronized (classLoader) {
				// If referencedBy was defined by a user-defined class loader, then that same user-defined class loader
				// initiates loading of the class denoted by className
				
				// First, the Java Virtual Machine determines whether classLoader has already been recorded as an
				// initiating loader of a class or interface denoted by name. If so, no class creation is necessary.
				Map<String, Klass> cache = getCache(classLoader);
				Klass cached = cache.get(name);
				if (cached != null) {
					return cached;
				}
				
				// TODO: Otherwise, the Java Virtual Machine invokes loadClass(name) on classLoader.
				Klass created = null; // classLoader.invoke()
				if (created == null) {
					throw new ClassNotFoundException();
				}
				cache.put(name, created);
				return created;
			}
		}
	}
}
