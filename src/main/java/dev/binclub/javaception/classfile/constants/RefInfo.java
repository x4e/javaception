package dev.binclub.javaception.classfile.constants;

public class RefInfo {
	public final int classIndex, nameAndTypeIndex;
	public ClassInfo classInfo;
	public NameAndTypeInfo nameAndTypeInfo;
	
	public RefInfo(int classIndex, int nameAndTypeIndex) {
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}
	
	public Object resolve(Object[] cp) {
		classInfo = (ClassInfo) cp[classIndex - 1];
		nameAndTypeInfo = (NameAndTypeInfo) cp[nameAndTypeIndex - 1];
		return this;
	}
}
