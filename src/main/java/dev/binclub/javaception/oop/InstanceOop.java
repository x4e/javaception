package dev.binclub.javaception.oop;

public class InstanceOop extends Oop {
	
	public Object[] fields;
	
	public InstanceOop(int fieldCount) {
		fields = new Object[fieldCount];
	}
	
	public static InstanceOop _null() {
		return null;
	}
}
