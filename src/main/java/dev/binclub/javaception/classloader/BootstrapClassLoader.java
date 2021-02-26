package dev.binclub.javaception.classloader;

import dev.binclub.javaception.classfile.Class;

import java.util.HashMap;

/**
 * The highest priority classloader on a vm.
 */
public class BootstrapClassLoader extends ClassLoader {
	HashMap<String, Class> dictionary = new HashMap<>();
	
	@Override
	public Class findClass(String name) {
		return dictionary.get(name);
	}
}
