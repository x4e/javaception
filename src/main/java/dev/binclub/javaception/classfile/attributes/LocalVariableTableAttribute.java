package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;

import java.io.DataInputStream;
import java.io.IOException;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_LocalVariableTable;


public class LocalVariableTableAttribute extends AttributeInfo {
	public final LocalVariableTable[] localVariableTable;
	
	public LocalVariableTableAttribute(LocalVariableTable[] localVariableTable) throws IOException {
		super(Attribute_LocalVariableTable);
		this.localVariableTable = localVariableTable;
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
