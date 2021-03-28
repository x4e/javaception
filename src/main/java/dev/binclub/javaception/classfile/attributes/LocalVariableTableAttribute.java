package dev.binclub.javaception.classfile.attributes;

import java.io.DataInputStream;
import java.io.IOException;

import dev.binclub.javaception.classfile.AttributeInfo;


public class LocalVariableTableAttribute extends AttributeInfo {
	int localVariableTableLength;
	LocalVariableTable[] localVariableTable;

	public LocalVariableTableAttribute(int attributeNameIndex, int attributeLength, DataInputStream dis)
			throws IOException {
		super(attributeNameIndex, attributeLength);
		localVariableTableLength = dis.readUnsignedShort();
		if (localVariableTableLength != 0) {
			localVariableTable = new LocalVariableTable[localVariableTableLength];
			for (int i = 0; i < localVariableTableLength; i++) {
				int startPc = dis.readUnsignedShort();
				int length = dis.readUnsignedShort();
				int nameIndex = dis.readUnsignedShort();
				int descriptorIndex = dis.readUnsignedShort();
				int index = dis.readUnsignedShort();
				localVariableTable[i] = new LocalVariableTable(startPc, length, nameIndex, descriptorIndex, index);
			}
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
