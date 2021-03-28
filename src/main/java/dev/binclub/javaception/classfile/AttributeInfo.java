package dev.binclub.javaception.classfile;

public abstract class AttributeInfo {
	public int attributeNameIndex, attributeLength;

	public AttributeInfo(int attributeNameIndex, int attributeLength) {
		this.attributeNameIndex = attributeNameIndex;
		this.attributeLength = attributeLength;
	}

	public String getAttributeName(Object[] cp) {
		return (String) cp[attributeNameIndex - 1];
	}

}
