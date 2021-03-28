package dev.binclub.javaception.classfile;

public class NameAndTypeInfo {
	int nameIndex;
	int descriptorIndex;

	public NameAndTypeInfo(int nameIndex, int descriptorIndex) {
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	public String getName(Object[] constantPool) {
		return (String) constantPool[nameIndex - 1];
	}

	public String getDescription(Object[] constantPool) {
		return (String) constantPool[descriptorIndex - 1];
	}

}
