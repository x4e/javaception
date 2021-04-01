package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.constants.UtfInfo;

import java.io.DataInputStream;
import java.io.IOException;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_SourceFile;

public class SourceFileAttribute extends AttributeInfo {
	public final String sourceFile;
	
	public SourceFileAttribute(String sourceFile) throws IOException {
		super(Attribute_SourceFile);
		this.sourceFile = sourceFile;
	}
}
