package dev.binclub.javaception.classfile.instructions;

import static dev.binclub.javaception.classfile.ClassFileConstants.TABLESWITCH;

public class TableSwitchInstruction extends SimpleInstruction {
	public final int high, low, dflt;
	public SimpleInstruction[] branches;
	public SimpleInstruction defaultInstruction;
	
	public TableSwitchInstruction(int high, int low, int dflt) {
		super(TABLESWITCH);
		this.high = high;
		this.low = low;
		this.dflt = dflt;
	}
	
	//to be used by the instruction parser
	public void setJumpOffsets(SimpleInstruction[] jumpOffsets) {
		this.branches = jumpOffsets;
	}
	
	public SimpleInstruction getDefaultInstruction() {
		return this.defaultInstruction;
	}
	
	//to be used by the instruction parser
	public void setDefaultInstruction(SimpleInstruction defaultInstruction) {
		this.defaultInstruction = defaultInstruction;
	}
}
