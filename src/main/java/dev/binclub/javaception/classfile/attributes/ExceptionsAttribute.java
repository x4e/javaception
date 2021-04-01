package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.constants.ClassInfo;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_Exceptions;


public class ExceptionsAttribute extends AttributeInfo {
	ClassInfo[] exceptionsTable;
	
	public ExceptionsAttribute(DataInputStream dis, Object[] constantPool) throws IOException {
		super(Attribute_Exceptions);
		int numberOfExceptions = dis.readUnsignedShort();
		exceptionsTable = new ClassInfo[numberOfExceptions];
		for (int i = 0; i < numberOfExceptions; i++) {
			int index = dis.readUnsignedShort();
			exceptionsTable[i] = (ClassInfo) constantPool[index - 1];
		}
		System.out.printf("Exceptions: %s%n", Arrays.toString(exceptionsTable));
	}
}
