package dev.binclub.javaception.oop;

import dev.binclub.javaception.klass.Klass;

public class InstanceOop extends Oop {
	private final Klass type;
	public final Object[] fields;
	
	public InstanceOop(Klass type, int fieldCount) {
		this.type = type;
		fields = new Object[fieldCount];
	}
	
	public Klass getKlass() {
		return type;
	}
	
	public static InstanceOop _null() {
		return null;
	}
}
