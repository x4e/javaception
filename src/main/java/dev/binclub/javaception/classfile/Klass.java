package dev.binclub.javaception.classfile;

import dev.binclub.javaception.oop.InstanceOop;

public class Klass {
	/**
	 * The defining loader of this class.
	 * Not necessarily the same as the initiating loader.
	 * See JVMS 5.3.
	 */
	public final InstanceOop classLoader;
	private final RuntimeConstantPool cp;
	private final String name;
	/**
	 * Super Class - probably shouldn't be null but who knows
	 */
	private final Klass superKlass;
	
	public Klass(InstanceOop classLoader, RuntimeConstantPool cp, String name, Klass superKlass) {
		this.classLoader = classLoader;
		this.cp = cp;
		this.name = name;
		this.superKlass = superKlass;
	}
}
