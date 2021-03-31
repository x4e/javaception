package dev.binclub.javaception.classfile.constants;

public class NameAndTypeInfo {
	public final int nameIndex, descriptorIndex;
	public String name, description;
	
	public NameAndTypeInfo(int nameIndex, int descriptorIndex) {
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
}
