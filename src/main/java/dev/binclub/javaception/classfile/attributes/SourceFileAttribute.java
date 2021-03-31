package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;

import java.io.DataInputStream;
import java.io.IOException;

public class SourceFileAttribute extends AttributeInfo {
	int sourceFileIndex;
	
	public SourceFileAttribute(int attributeLength, DataInputStream dis) throws IOException {
		super("SourceFile", attributeLength);
		this.sourceFileIndex = dis.readUnsignedShort();
	}
	
	public String getName(Object[] cp) {
		return (String) cp[sourceFileIndex - 1];
	}
	
}
