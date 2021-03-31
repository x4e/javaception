package dev.binclub.javaception.classfile.constants;

import dev.binclub.javaception.classfile.attributes.BootstrapMethodsAttribute.BootstrapMethod;

public class DynamicInfo {
	public final int bootstrapMethodAttrIndex, nameAndTypeIndex;
	public NameAndTypeInfo nameAndTypeInfo;
	
	public DynamicInfo(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
		this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}
	
	public BootstrapMethod getBootstrapMethod(BootstrapMethod[] bootstrapMethods) {
		// potentially need to sub one from attrIndex?
		return bootstrapMethods[bootstrapMethodAttrIndex];
	}
	
	public NameAndTypeInfo getFieldNameAndTypeInfo(Object[] constantPool) {
		return nameAndTypeInfo;
	}
}