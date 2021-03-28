package dev.binclub.javaception.classfile.attributes;

import java.io.DataInputStream;
import java.io.IOException;

import dev.binclub.javaception.classfile.AttributeInfo;
	

public class StackMapTableAttribute extends AttributeInfo {

	int numberOfEntries;

	public StackMapTableAttribute(int attributeNameIndex, int attributeLength, DataInputStream dis) throws IOException {
		super(attributeNameIndex, attributeLength);
		numberOfEntries = dis.readUnsignedShort();
		for (int i = 0; i < attributeLength - 2; i++) {
			
			// todo
			dis.readUnsignedByte();
		}
	}
}
