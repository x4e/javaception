package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.constants.MethodHandleInfo;

import java.io.DataInputStream;
import java.io.IOException;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_BootstrapMethods;

public class BootstrapMethodsAttribute extends AttributeInfo {
	public final BootstrapMethod[] bootstrapMethods;
	
	public BootstrapMethodsAttribute(BootstrapMethod[] bootstrapMethods) throws IOException {
		super(Attribute_BootstrapMethods);
		this.bootstrapMethods = bootstrapMethods;
	}
	
	public static class BootstrapMethod {
		MethodHandleInfo bootstrapMethodRef;
		Object[] bootstrapArguments;
		
		public BootstrapMethod(MethodHandleInfo bootstrapMethodRef, Object[] bootstrapArguments) {
			this.bootstrapMethodRef = bootstrapMethodRef;
			this.bootstrapArguments = bootstrapArguments;
		}
		
	}
}
