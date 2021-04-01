package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.constants.ClassInfo;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_Exceptions;


public class ExceptionsAttribute extends AttributeInfo {
	public final ClassInfo[] exceptionsTable;
	
	public ExceptionsAttribute(ClassInfo[] exceptionsTable) throws IOException {
		super(Attribute_Exceptions);
		this.exceptionsTable = exceptionsTable;
	}
}
