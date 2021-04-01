package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;

import java.io.DataInputStream;
import java.io.IOException;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_LineNumberTable;


public class LineNumberTableAttribute extends AttributeInfo {
	public final LineInfo[] lineNumberTable;
	
	public LineNumberTableAttribute(LineInfo[] lineNumberTable) throws IOException {
		super(Attribute_LineNumberTable);
		this.lineNumberTable = lineNumberTable;
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
