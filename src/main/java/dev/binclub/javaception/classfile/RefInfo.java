package dev.binclub.javaception.classfile;

public class RefInfo {
	int classIndex;
	int nameAndTypeIndex;

	public RefInfo(int classIndex, int nameAndTypeIndex) {
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public ClassInfo getClassInfo(Object[] constantPool) {
		return (ClassInfo) constantPool[classIndex - 1];
	}
	
	public NameAndTypeInfo getNameAndTypeInfo(Object[] constantPool) {
		return (NameAndTypeInfo) constantPool[nameAndTypeIndex - 1];
	}

}
