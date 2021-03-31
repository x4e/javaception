package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;

import java.io.DataInputStream;
import java.io.IOException;


public class ConstantValueAttribute extends AttributeInfo {
	
	int constantValueIndex;
	
	public ConstantValueAttribute(String name, int attributeLength, DataInputStream dis) throws IOException {
		super(name);
		constantValueIndex = dis.readUnsignedShort();
	}
	
	public Object getConst(Object[] cp) {
		return cp[constantValueIndex - 1];
	}
}
