package dev.binclub.javaception.classfile.instructions;

public class BranchInstruction extends SimpleInstruction{

	SimpleInstruction branchInstruction;
	int branchPosition;
	
	public BranchInstruction(int opcode, int branchOffset) {
		super(opcode);
		this.branchPosition = branchOffset;
	}
	
	public SimpleInstruction getBranchInstruction() {
		return branchInstruction;
	}
	//branchOffset
	public void setBranchInstruction(SimpleInstruction branchInstruction) {
		this.branchInstruction = branchInstruction;
	}
	
	public int getBranchPosition() {
		return branchPosition;
	}
	
}
