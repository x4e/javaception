package dev.binclub.javaception.classfile.attributes;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.classfile.ExceptionData;
import dev.binclub.javaception.classfile.OpcodeStride;

public class CodeAttribute extends AttributeInfo {

	int maxStack;
	int maxLocals;
	int codeLength;
	int[] code;
	int exceptionTableLength;
	ExceptionData[] exceptions;
	int attributesCount;
	AttributeInfo[] attributes;

	public CodeAttribute(int attributeNameIndex, int attributeLength, DataInputStream dis, Object[] constantPool)
			throws IOException {
		super(attributeNameIndex, attributeLength);
		maxStack = dis.readUnsignedShort();
		maxLocals = dis.readUnsignedShort();
		codeLength = dis.readInt();
		code = new int[codeLength];
		for (int i = 0; i < codeLength; i++) {
			code[i] = dis.readUnsignedByte();
		}
		exceptionTableLength = dis.readUnsignedShort();
		if (exceptionTableLength != 0) {
			exceptions = new ExceptionData[exceptionTableLength];
			for (int i = 0; i < exceptionTableLength; i++) {
				int startPc = dis.readUnsignedShort();
				int endPc = dis.readUnsignedShort();
				int handlerPc = dis.readUnsignedShort();
				int catchType = dis.readUnsignedShort();
				exceptions[i] = new ExceptionData(startPc, endPc, handlerPc, catchType);
			}
		}
		attributesCount = dis.readUnsignedShort();
		if (attributesCount != 0) {
			attributes = new AttributeInfo[attributesCount];
			for (int i = 0; i < attributesCount; i++) {
				attributes[i] = ClassFileParser.readAttribute(dis, constantPool);
			}
		}
	}
	
	public int getMaxStack() {
		return maxStack;
	}
	
	public int getMaxLocals() {
		return maxLocals;
	}
	
	public int[] getCode() {
		return code;
	}

}
