package dev.binclub.javaception.classfile;

import java.util.List;

public class FieldInfo {
	public final int access;
	public final String name;
	public final String descriptor;
	public final List<AttributeInfo> attributes;
	
	public FieldInfo(int access, String name, String descriptor, List<AttributeInfo> attributes) {
		this.access = access;
		this.name = name;
		this.descriptor = descriptor;
		this.attributes = attributes;
	}
}
