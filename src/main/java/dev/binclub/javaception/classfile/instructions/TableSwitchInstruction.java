package dev.binclub.javaception.classfile.instructions;

public class TableSwitchInstruction extends SimpleInstruction {

	SimpleInstruction[] branches;
	SimpleInstruction defaultInstruction;
	//misspelled on purpose
	public final int high, low, defalt;

	public TableSwitchInstruction(int high, int low, int defalt) {
		super(0xaa);
		this.high = high;
		this.low = low;
		this.defalt = defalt;
	}

	public SimpleInstruction[] getJumpOffsets() {
		return branches;
	}
	
	public SimpleInstruction getDefaultInstruction() {
		return this.defaultInstruction;
	}
	
	//to be used by the instruction parser
	public void setJumpOffsets(SimpleInstruction[] jumpOffsets) {
		this.branches = jumpOffsets;
	}
	//to be used by the instruction parser
	public void setDefaultInstruction(SimpleInstruction defaultInstruction) {
		this.defaultInstruction = defaultInstruction;
	}

}
