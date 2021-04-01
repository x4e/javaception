package dev.binclub.javaception.classfile.instructions;

public class LookupSwitchInstruction extends SimpleInstruction {
	
	public final int defalt, npairs;
	int[] keys;
	SimpleInstruction[] branches;
	SimpleInstruction defaultInstruction;
	
	public LookupSwitchInstruction(int defalt, int npairs) {
		super(0xab);
		this.defalt = defalt;
		this.npairs = npairs;
	}
	
	public SimpleInstruction[] getJumpOffsets() {
		return branches;
	}
	
	//to be used by the instruction parser
	public void setJumpOffsets(SimpleInstruction[] jumpOffsets) {
		this.branches = jumpOffsets;
	}
	
	public int[] getKeys() {
		return keys;
	}
	
	//to be used by the instruction parser
	public void setKeys(int[] keys) {
		this.keys = keys;
	}
	
	public SimpleInstruction getDefaultInstruction() {
		return this.defaultInstruction;
	}
	
	//to be used by the instruction parser
	public void setDefaultInstruction(SimpleInstruction defaultInstruction) {
		this.defaultInstruction = defaultInstruction;
	}
	
}
