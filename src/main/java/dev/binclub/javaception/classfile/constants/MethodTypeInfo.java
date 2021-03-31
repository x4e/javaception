package dev.binclub.javaception.classfile.constants;

public class MethodTypeInfo {
	public final int descriptorIndex;
	public String methodDescription;
	
	public MethodTypeInfo(int descriptorIndex) {
		this.descriptorIndex = descriptorIndex;
	}
	
	public String getMethodDescription() {
		return methodDescription;
	}
	
}
