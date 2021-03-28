package dev.binclub.javaception.classfile.attributes;

import java.io.DataInputStream;
import java.io.IOException;

import dev.binclub.javaception.classfile.AttributeInfo;
import dev.binclub.javaception.classfile.MethodHandleInfo;

public class BootstrapMethodsAttribute extends AttributeInfo {

	int numBootstrapMethods;
	BootstrapMethod[] bootstrapMethods;

	public BootstrapMethodsAttribute(int attributeNameIndex, int attributeLength, DataInputStream dis,
			Object[] constantPool) throws IOException {
		super(attributeNameIndex, attributeLength);
		numBootstrapMethods = dis.readUnsignedShort();
		bootstrapMethods = new BootstrapMethod[numBootstrapMethods];
		for (int i = 0; i < numBootstrapMethods; i++) {
			int bootstrapMethodRef = dis.readUnsignedShort();
			int numBootstrapArguments = dis.readUnsignedShort();
			MethodHandleInfo[] bootstrapArguments = new MethodHandleInfo[numBootstrapArguments];
			for (int j = 0; j < numBootstrapArguments; j++) {
				int index = dis.readUnsignedShort();
				bootstrapArguments[j] = (MethodHandleInfo) constantPool[index - 1];
			}
			bootstrapMethods[i] = new BootstrapMethod(bootstrapMethodRef, numBootstrapArguments, bootstrapArguments);
		}
	}

	public static class BootstrapMethod {
		int bootstrapMethodRef;
		int numBootstrapArguments;
		MethodHandleInfo[] bootstrapArguments;

		public BootstrapMethod(int bootstrapMethodRef, int numBootstrapArguments,
				MethodHandleInfo[] bootstrapArguments) {
			super();
			this.bootstrapMethodRef = bootstrapMethodRef;
			this.numBootstrapArguments = numBootstrapArguments;
			this.bootstrapArguments = bootstrapArguments;
		}

	}

}
