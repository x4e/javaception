package dev.binclub.javaception.classloader;

import dev.binclub.javaception.classfile.Class;

/**
 * Used to lookup classes, typically from a file system
 */
public abstract class ClassLoader {
	/**
	 * Attempts to find a class.
	 * If it cannot be found null is returned.
	 * @param name the fully qualified (.) name of the class
	 * @return the instance of this class
	 * @throws ClassNotFoundException if the class could not be found
	 */
	public abstract Class findClass(String name) throws ClassNotFoundException;
}
