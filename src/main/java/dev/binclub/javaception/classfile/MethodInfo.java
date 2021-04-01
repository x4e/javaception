package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classfile.attributes.CodeAttribute;

import java.util.List;

public class MethodInfo {
	public final int access;
	public final String name;
	public final String descriptor;
	List<AttributeInfo> attributes;
	// for fast lookup
	CodeAttribute codeAttribute;
	
	public MethodInfo(int access, String name, String descriptor, List<AttributeInfo> attributes) {
		this.access = access;
		this.name = name;
		this.descriptor = descriptor;
		this.attributes = attributes;
		for (AttributeInfo attribute : attributes) {
			if (attribute instanceof CodeAttribute) {
				codeAttribute = (CodeAttribute) attribute;
				break;
			}
		}
	}
	
	public CodeAttribute getCodeAttribute() {
		return codeAttribute;
	}
	
}
