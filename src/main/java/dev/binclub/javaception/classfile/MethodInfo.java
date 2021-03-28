package dev.binclub.javaception.classfile;

import java.util.List;

public class MethodInfo {
	int access;
	int nameIndex;
	int descriptorIndex;
	List<AttributeInfo> attributes;

	public MethodInfo(int access, int nameIndex, int descriptorIndex, List<AttributeInfo> attributes) {
		this.access = access;
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		this.attributes = attributes;
	}
}
