package dev.binclub.javaception.classfile.instructions;

public class SimpleInstruction {
	
	int opcode;
	//ret instructions will lack this
	SimpleInstruction nextInstruction;
	
	public SimpleInstruction(int opcode) {
		this.opcode = opcode;
	}
	
	public int getOpcode() {
		return opcode;
	}
	
	public SimpleInstruction getNextInstruction() {
		return nextInstruction;
	}
	
	//for use by instructionparser
	public void setNextInstruction(SimpleInstruction nextInstruction) {
		this.nextInstruction = nextInstruction;
	}
}
