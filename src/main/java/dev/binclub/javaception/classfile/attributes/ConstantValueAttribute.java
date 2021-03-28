package dev.binclub.javaception.classfile.attributes;

import java.io.DataInputStream;
import java.io.IOException;

import dev.binclub.javaception.classfile.AttributeInfo;


public class ConstantValueAttribute extends AttributeInfo {

	int constantValueIndex;

	public ConstantValueAttribute(int attributeNameIndex, int attributeLength, DataInputStream dis) throws IOException {
		super(attributeNameIndex, attributeLength);
		constantValueIndex = dis.readUnsignedShort();
	}

	public Object getConst(Object[] cp) {
		return cp[constantValueIndex - 1];
	}
}
