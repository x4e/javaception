package dev.binclub.javaception.classfile.instructions;

public class VarInstruction extends SimpleInstruction {
	
	int value;
	
	public VarInstruction(int opcode, int value) {
		super(opcode);
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
}
