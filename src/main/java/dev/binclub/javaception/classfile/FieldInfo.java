package dev.binclub.javaception.classfile;

public class FieldInfo {
	public final int access;
	public final String name;
	public final String descriptor;
	
	public Object constantValue;
	
	public FieldInfo(int access, String name, String descriptor) {
		this.access = access;
		this.name = name;
		this.descriptor = descriptor;
	}
}
