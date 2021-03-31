package dev.binclub.javaception.classfile.instructions;

import dev.binclub.javaception.classfile.constants.RefInfo;

public class RefInstruction extends SimpleInstruction {
	RefInfo reference;
	
	public RefInstruction(int opcode, RefInfo fieldRefence) {
		super(opcode);
	}
	
	public RefInfo getReferenceInfo() {
		return reference;
	}
	
}
