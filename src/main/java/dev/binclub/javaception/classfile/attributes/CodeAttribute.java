package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.classfile.ExceptionData;
import dev.binclub.javaception.classfile.InstructionParser;
import dev.binclub.javaception.classfile.instructions.SimpleInstruction;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class CodeAttribute extends AttributeInfo {
	
	int maxStack;
	int maxLocals;
	int codeLength;
	List<SimpleInstruction> instructions;
	int exceptionTableLength;
	ExceptionData[] exceptions;
	int attributesCount;
	AttributeInfo[] attributes;
	
	public CodeAttribute(int attributeLength, DataInputStream dis, Object[] constantPool)
		throws IOException {
		super("Code", attributeLength);
		maxStack = dis.readUnsignedShort();
		maxLocals = dis.readUnsignedShort();
		codeLength = dis.readInt();
		int[] code = new int[codeLength];
		for (int i = 0; i < codeLength; i++) {
			code[i] = dis.readUnsignedByte();
		}
		exceptionTableLength = dis.readUnsignedShort();
		exceptions = new ExceptionData[exceptionTableLength];
		for (int i = 0; i < exceptionTableLength; i++) {
			int startPc = dis.readUnsignedShort();
			int endPc = dis.readUnsignedShort();
			int handlerPc = dis.readUnsignedShort();
			int catchType = dis.readUnsignedShort();
			exceptions[i] = new ExceptionData(startPc, endPc, handlerPc, catchType);
		}
		attributesCount = dis.readUnsignedShort();
		attributes = new AttributeInfo[attributesCount];
		for (int i = 0; i < attributesCount; i++) {
			attributes[i] = ClassFileParser.readAttribute(dis, constantPool);
		}
		instructions = InstructionParser.parseCode(code, constantPool);
	}
	
	public int getMaxStack() {
		return maxStack;
	}
	
	public int getMaxLocals() {
		return maxLocals;
	}
	
	public List<SimpleInstruction> getInstructions() {
		return instructions;
	}
	
}
