package dev.binclub.javaception.classfile.constants;

public class DynamicInfo {
	public final int bootstrapMethodAttrIndex, nameAndTypeIndex;
	public NameAndTypeInfo nameAndTypeInfo;
	
	public DynamicInfo(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
		this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}
	
	public Object resolve(Object[] cp) {
		nameAndTypeInfo = (NameAndTypeInfo) cp[nameAndTypeIndex - 1];
		return this;
	}
}
