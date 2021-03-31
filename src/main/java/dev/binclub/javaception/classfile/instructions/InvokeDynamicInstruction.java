package dev.binclub.javaception.classfile.instructions;

import dev.binclub.javaception.classfile.constants.InvokeDynamicInfo;

public class InvokeDynamicInstruction extends SimpleInstruction {
	
	InvokeDynamicInfo invokeDynamicInfo;
	
	public InvokeDynamicInstruction(InvokeDynamicInfo invokeDynamicInfo) {
		super(0xba);
		this.invokeDynamicInfo = invokeDynamicInfo;
	}
	
	public InvokeDynamicInfo getInvokeDynamicInfo() {
		return invokeDynamicInfo;
	}
	
}
