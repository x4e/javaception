package dev.binclub.javaception.classfile.instructions;

public class LoadConstantInstruction extends SimpleInstruction {
	Object constant;

	public LoadConstantInstruction(int opcode, Object constant) {
		super(opcode);
		this.constant = constant;
	}

}
