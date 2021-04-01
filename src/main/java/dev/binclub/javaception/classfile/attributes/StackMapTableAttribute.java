package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;

import java.io.DataInputStream;
import java.io.IOException;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_StackMapTable;

public class StackMapTableAttribute extends AttributeInfo {
	public StackMapTableAttribute() {
		super(Attribute_StackMapTable);
	}
}
