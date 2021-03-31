package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;

import java.io.DataInputStream;
import java.io.IOException;

public class StackMapTableAttribute extends AttributeInfo {
	
	int numberOfEntries;
	
	public StackMapTableAttribute(int attributeLength, DataInputStream dis) throws IOException {
		super("StackMapTable", attributeLength);
		numberOfEntries = dis.readUnsignedShort();
		dis.skipBytes(attributeLength - 2);
	}
}
