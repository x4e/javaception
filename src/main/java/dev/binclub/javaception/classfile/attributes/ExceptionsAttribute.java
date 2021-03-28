package dev.binclub.javaception.classfile.attributes;

import java.io.DataInputStream;
import java.io.IOException;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.ClassInfo;


public class ExceptionsAttribute extends AttributeInfo {

	int numberOfExceptions;
	ClassInfo[] exceptionIndexTable;

	public ExceptionsAttribute(int attributeNameIndex, int attributeLength, DataInputStream dis, Object[] constantPool)
			throws IOException {
		super(attributeNameIndex, attributeLength);
		numberOfExceptions = dis.readUnsignedShort();
		if (numberOfExceptions != 0) {
			exceptionIndexTable = new ClassInfo[numberOfExceptions];
			for (int i = 0; i < numberOfExceptions; i++) {
				int index = dis.readUnsignedShort();
				exceptionIndexTable[i] = (ClassInfo) constantPool[index - 1];
			}
		}

	}

}
