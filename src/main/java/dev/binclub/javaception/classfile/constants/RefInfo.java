package dev.binclub.javaception.classfile.constants;

public class RefInfo {
	public final int classIndex, nameAndTypeIndex;
	public ClassInfo classInfo;
	public NameAndTypeInfo nameAndTypeInfo;

	public RefInfo(int classIndex, int nameAndTypeIndex) {
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public ClassInfo getClassInfo() {
		return classInfo;
	}
	
	public NameAndTypeInfo getNameAndTypeInfo() {
		return nameAndTypeInfo;
	}

}
