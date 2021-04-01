package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.classfile.instructions.SimpleInstruction;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_Code;
import static dev.binclub.javaception.utils.ByteUtils.*;

public class CodeAttribute extends AttributeInfo {
	int maxStack;
	int maxLocals;
	byte[] code;
	int codeOffset;
	int codeEnd;
	List<SimpleInstruction> instructions;
	ExceptionData[] exceptions;
	List<AttributeInfo> attributes;
	
	public CodeAttribute(ClassFileParser parser, byte[] data, int offset, Object[] constantPool) throws IOException {
		super(Attribute_Code);
		maxStack = readUnsignedShort(data, offset);
		maxLocals = readUnsignedShort(data, offset + 2);
		int codeLength = readInt(data, offset + 4);
		codeOffset = offset + 8;
		codeEnd = codeOffset + codeLength;
		int exceptionTableLength = readUnsignedShort(data, codeEnd);
		offset = codeEnd + 2;
		exceptions = new ExceptionData[exceptionTableLength];
		for (int i = 0; i < exceptionTableLength; i++) {
			int startPc = readUnsignedShort(data, offset);
			int endPc = readUnsignedShort(data, offset + 2);
			int handlerPc = readUnsignedShort(data, offset + 4);
			int catchType = readUnsignedShort(data, offset + 6);
			exceptions[i] = new ExceptionData(startPc, endPc, handlerPc, catchType);
			offset += 8;
		}
		attributes = parser.readAttributes(offset, constantPool, 3);
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
	
	private static class ExceptionData {
		int startPc;
		int endPc;
		int handlerPc;
		int catchType;
		
		public ExceptionData(int startPc, int endPc, int handlerPc, int catchType) {
			super();
			this.startPc = startPc;
			this.endPc = endPc;
			this.handlerPc = handlerPc;
			this.catchType = catchType;
		}
	}
}
