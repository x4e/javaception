package dev.binclub.javaception.classfile.instructions;

import dev.binclub.javaception.classfile.constants.ClassInfo;

public class MultiANewArray extends ClassRefInstruction {

	int dimensions;

	public MultiANewArray(ClassInfo classReference, int dimensions) {
		super(0xc5, classReference);
	}

	public int getDimensions() {
		return dimensions;
	}

}
