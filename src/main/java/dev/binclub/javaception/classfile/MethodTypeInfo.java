package dev.binclub.javaception.classfile;

public class MethodTypeInfo {
	int descriptorIndex;
	
	public MethodTypeInfo(int descriptorIndex) {
		this.descriptorIndex = descriptorIndex;
	}
	
	public String getMethodDescription(Object[] constantPool) {
		return (String) constantPool[descriptorIndex - 1];
	}
	
}
