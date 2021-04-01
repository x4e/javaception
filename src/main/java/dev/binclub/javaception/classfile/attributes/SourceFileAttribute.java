package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.constants.UtfInfo;

import java.io.DataInputStream;
import java.io.IOException;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_SourceFile;

public class SourceFileAttribute extends AttributeInfo {
	String sourceFile;
	
	public SourceFileAttribute(DataInputStream dis, Object[] cp) throws IOException {
		super(Attribute_SourceFile);
		this.sourceFile = ((UtfInfo) cp[dis.readUnsignedShort() - 1]).get();
	}
}
