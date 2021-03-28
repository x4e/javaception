package dev.binclub.javaception.classfile;

import java.io.IOException;

public abstract class AttributeInfo {
	public int attributeNameIndex, attributeLength;

	public AttributeInfo(int attributeNameIndex, int attributeLength) throws IOException{
		this.attributeNameIndex = attributeNameIndex;
		this.attributeLength = attributeLength;
	}

	public String getAttributeName(Object[] cp) {
		return (String) cp[attributeNameIndex - 1];
	}

}
