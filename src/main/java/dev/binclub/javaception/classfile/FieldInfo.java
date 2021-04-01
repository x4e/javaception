package dev.binclub.javaception.classfile;

import java.util.List;

public class FieldInfo {
	public final  int access;
	public final  int nameIndex;
	public final  int descriptorIndex;
	public final List<AttributeInfo> attributes;
	
	public FieldInfo(int access, int nameIndex, int descriptorIndex, List<AttributeInfo> attributes) {
		this.access = access;
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		this.attributes = attributes;
	}
}
