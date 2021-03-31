package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classfile.attributes.CodeAttribute;

import java.util.List;

public class MethodInfo {
	public int access;
	int nameIndex;
	int descriptorIndex;
	List<AttributeInfo> attributes;
	// for fast lookup
	CodeAttribute codeAttribute;
	
	public MethodInfo(int access, int nameIndex, int descriptorIndex, List<AttributeInfo> attributes) {
		this.access = access;
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		this.attributes = attributes;
		for (AttributeInfo attribute : attributes) {
			if (attribute instanceof CodeAttribute) {
				codeAttribute = (CodeAttribute) attribute;
				break;
			}
		}
	}
	
	public String getName(Object[] constantPool) {
		return (String) constantPool[nameIndex - 1];
	}
	
	public String getSignature(Object[] constantPool) {
		return (String) constantPool[descriptorIndex - 1];
	}
	
	public CodeAttribute getCodeAttribute() {
		return codeAttribute;
	}
	
}
