package dev.binclub.javaception.classfile.constants;

public class MethodTypeInfo {
	public final int descriptorIndex;
	public String descriptor;
	
	public MethodTypeInfo(int descriptorIndex) {
		this.descriptorIndex = descriptorIndex;
	}
	
	public Object resolve(Object[] cp) {
		descriptor = (String) cp[descriptorIndex - 1];
		return this;
	}
}
