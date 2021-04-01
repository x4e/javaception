package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;

import java.io.DataInputStream;
import java.io.IOException;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_ConstantValue;


public class ConstantValueAttribute extends AttributeInfo {
	public final Object constantValue;
	
	public ConstantValueAttribute(Object constantValue) throws IOException {
		super(Attribute_ConstantValue);
		this.constantValue = constantValue;
	}
}
