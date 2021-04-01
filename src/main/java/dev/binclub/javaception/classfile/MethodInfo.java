package dev.binclub.javaception.classfile;

public class MethodInfo {
	public final int access;
	public final String name;
	public final String descriptor;
	public CodeAttribute code;
	
	public MethodInfo(int access, String name, String descriptor) {
		this.access = access;
		this.name = name;
		this.descriptor = descriptor;
	}
}
