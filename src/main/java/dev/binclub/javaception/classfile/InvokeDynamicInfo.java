package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classfile.attributes.BootstrapMethodsAttribute.BootstrapMethod;

public class InvokeDynamicInfo {
	int bootstrapMethodAttrIndex;
	int nameAndTypeIndex;

	public InvokeDynamicInfo(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
		this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public BootstrapMethod getBootstrapMethod(BootstrapMethod[] bootstrapMethods) {
		// potentially need to sub one from attrIndex?
		return bootstrapMethods[bootstrapMethodAttrIndex];
	}

	public NameAndTypeInfo getMethodNameAndTypeInfo(Object[] constantPool) {
		return (NameAndTypeInfo) constantPool[nameAndTypeIndex - 1];
	}
}
