package dev.binclub.javaception.classfile;

public class MethodInfo {
	int access;
	int nameIndex;
	int descriptorIndex;
	// may be null
	AttributeInfo[] attributes;

	public MethodInfo(int access, int nameIndex, int descriptorIndex, AttributeInfo[] attributes) {
		this.access = access;
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		this.attributes = attributes;
	}
}
