package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.classfile.constants.MethodHandleInfo;
import dev.binclub.javaception.classfile.constants.UtfInfo;
import dev.binclub.javaception.classfile.instructions.SimpleInstruction;

import java.util.List;

import static dev.binclub.javaception.classfile.ClassFileConstants.*;
import static dev.binclub.javaception.utils.ByteUtils.*;

public class CodeAttribute extends AttributeInfo {
	int maxStack;
	int maxLocals;
	byte[] code;
	int codeOffset;
	int codeEnd;
	List<SimpleInstruction> instructions;
	ExceptionData[] exceptions;
	
	public LineInfo[] lineNumberTable;
	public LocalVariableTable[] localVariableTable;
	
	public CodeAttribute(ClassFileParser parser, byte[] data, int offset, Object[] constantPool) {
		super(Attribute_Code);
		maxStack = readUnsignedShort(data, offset);
		maxLocals = readUnsignedShort(data, offset + 2);
		int codeLength = readInt(data, offset + 4);
		code = data;
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
		readCodeAttributes(data, offset, constantPool);
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
	
	
	public int readCodeAttributes(byte[] data, int offset, Object[] constantPool) {
		int attributesCount = readUnsignedShort(data, offset);
		offset += 2;
		while (attributesCount-- > 0) {
			int attributeNameIndex = readUnsignedShort(data, offset);
			int attributeLength = readInt(data, offset + 2);
			offset += 6;
			int attributeStart = offset;
			
			String name = ((UtfInfo) constantPool[attributeNameIndex - 1]).get();
			switch (name) {
			case Attribute_LineNumberTable -> {
				int lineNumberTableLength = readUnsignedShort(data, offset);
				offset += 2;
				lineNumberTable = new LineInfo[lineNumberTableLength];
				for (int i = 0; i < lineNumberTableLength; i++) {
					int startPc = readUnsignedShort(data, offset);
					int lineNumber = readUnsignedShort(data, offset + 2);
					offset += 4;
					lineNumberTable[i] = new LineInfo(startPc, lineNumber);
				}
			}
			case Attribute_LocalVariableTable -> {
				int localVariableTableLength = readUnsignedShort(data, offset);
				offset += 2;
				localVariableTable = new LocalVariableTable[localVariableTableLength];
				for (int i = 0; i < localVariableTableLength; i++) {
					int startPc = readUnsignedShort(data, offset);
					int length = readUnsignedShort(data, offset);
					int nameIndex = readUnsignedShort(data, offset);
					int descriptorIndex = readUnsignedShort(data, offset);
					int index = readUnsignedShort(data, offset);
					offset += 10;
					localVariableTable[i] = new LocalVariableTable(startPc, length, nameIndex, descriptorIndex, index);
				}
			}}
			
			offset = attributeStart + attributeLength;
		}
		return offset;
	}
	
	public static class LineInfo {
		int startPc, lineNumber;
		
		public LineInfo(int startPc, int lineNumber) {
			super();
			this.startPc = startPc;
			this.lineNumber = lineNumber;
		}
	}
	
	public static class LocalVariableTable {
		int startPc, length, nameIndex, descriptorIndex, index;
		
		public LocalVariableTable(int startPc, int length, int nameIndex, int descriptorIndex, int index) {
			this.startPc = startPc;
			this.length = length;
			this.nameIndex = nameIndex;
			this.descriptorIndex = descriptorIndex;
			this.index = index;
		}
	}
}
