package dev.binclub.javaception.classfile.instructions;

public class IincInstruction extends SimpleInstruction{

	int index;
	int increment;
	
	public IincInstruction(int index, int increment) {
		super(0x84);
		this.index = index;
		this.increment = increment;
	}
	
}
