package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.constants.ClassInfo;

import java.io.DataInputStream;
import java.io.IOException;


public class ExceptionsAttribute extends AttributeInfo {
	
	int numberOfExceptions;
	ClassInfo[] exceptionIndexTable;
	
	public ExceptionsAttribute(int attributeLength, DataInputStream dis, Object[] constantPool)
		throws IOException {
		super("Exceptions");
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
