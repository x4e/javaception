package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;

import java.io.DataInputStream;
import java.io.IOException;


public class LineNumberTableAttribute extends AttributeInfo {
	
	int lineNumberTableLength;
	LineInfo[] lineNumberTable;
	
	public LineNumberTableAttribute(int attributeLength, DataInputStream dis) throws IOException {
		super("LineNumberTable");
		lineNumberTableLength = dis.readUnsignedShort();
		if (lineNumberTableLength != 0) {
			lineNumberTable = new LineInfo[lineNumberTableLength];
			for (int i = 0; i < lineNumberTableLength; i++) {
				int startPc = dis.readUnsignedShort();
				int lineNumber = dis.readUnsignedShort();
				lineNumberTable[i] = new LineInfo(startPc, lineNumber);
			}
		}
		
	}
	
	public static class LineInfo {
		int startPc, lineNumber;
		
		public LineInfo(int startPc, int lineNumber) {
			super();
			this.startPc = startPc;
			this.lineNumber = lineNumber;
		}
	}
	
}
