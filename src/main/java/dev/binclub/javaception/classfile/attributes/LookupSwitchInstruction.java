package dev.binclub.javaception.classfile.attributes;

import dev.binclub.javaception.classfile.instructions.SimpleInstruction;

public class LookupSwitchInstruction extends SimpleInstruction {

	int[] keys;
	SimpleInstruction[] branches;
	public final int defalt, npairs;
	SimpleInstruction defaultInstruction;

	public LookupSwitchInstruction(int defalt, int npairs) {
		super(0xab);
		this.defalt = defalt;
		this.npairs = npairs;
	}
	
	public SimpleInstruction[] getJumpOffsets() {
		return branches;
	}
	
	public int[] getKeys() {
		return keys;
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
	//to be used by the instruction parser
	public void setKeys(int[] keys) {
		this.keys = keys;
	}

}
