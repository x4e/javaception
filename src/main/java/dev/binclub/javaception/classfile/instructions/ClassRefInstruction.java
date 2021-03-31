package dev.binclub.javaception.classfile.instructions;

import dev.binclub.javaception.classfile.constants.ClassInfo;

//check cast // instanceof // new // anewarray all use this
public class ClassRefInstruction extends SimpleInstruction {
	
	ClassInfo classReference;
	
	public ClassRefInstruction(int opcode, ClassInfo classReference) {
		super(opcode);
		this.classReference = classReference;
	}
	
	public ClassInfo getClassReference() {
		return classReference;
	}
	
}
