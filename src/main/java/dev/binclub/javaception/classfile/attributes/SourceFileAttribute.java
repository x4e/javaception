package dev.binclub.javaception.classfile.attributes;

import java.io.DataInputStream;
import java.io.IOException;

import dev.binclub.javaception.classfile.AttributeInfo;

public class SourceFileAttribute extends AttributeInfo {
	int sourceFileIndex;

	public SourceFileAttribute(int attributeNameIndex, int attributeLength, DataInputStream dis) throws IOException {
		super(attributeNameIndex, attributeLength);
		this.sourceFileIndex = dis.readUnsignedShort();
	}

	public String getName(Object[] cp) {
		return (String) cp[sourceFileIndex - 1];
	}

}