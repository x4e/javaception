package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.constants.MethodHandleInfo;

import java.io.DataInputStream;
import java.io.IOException;

import static dev.binclub.javaception.classfile.ClassFileConstants.Attribute_BootstrapMethods;

public class BootstrapMethodsAttribute extends AttributeInfo {
	BootstrapMethod[] bootstrapMethods;
	
	public BootstrapMethodsAttribute(DataInputStream dis, Object[] constantPool) throws IOException {
		super(Attribute_BootstrapMethods);
		int numBootstrapMethods = dis.readUnsignedShort();
		bootstrapMethods = new BootstrapMethod[numBootstrapMethods];
		for (int i = 0; i < numBootstrapMethods; i++) {
			int bootstrapMethodRef = dis.readUnsignedShort();
			MethodHandleInfo methodHandleInfo = (MethodHandleInfo) constantPool[bootstrapMethodRef - 1];
			int numBootstrapArguments = dis.readUnsignedShort();
			Object[] bootstrapArguments = new Object[numBootstrapArguments];
			for (int j = 0; j < numBootstrapArguments; j++) {
				int index = dis.readUnsignedShort();
				bootstrapArguments[j] = constantPool[index - 1];
			}
			bootstrapMethods[i] = new BootstrapMethod(methodHandleInfo, bootstrapArguments);
		}
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
