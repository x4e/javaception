package dev.binclub.javaception.classfile;

public abstract class AttributeInfo {
	public int attributeLength;
	String name;

	public AttributeInfo(String name, int attributeLength) {
		this.name = name;
		this.attributeLength = attributeLength;
	}

	public String getAttributeName() {
		return name;
	}

}
