package dev.binclub.javaception.runtime;

import dev.binclub.javaception.classfile.instructions.SimpleInstruction;

public interface InstructionExecutor {
	/*
	 * @return return null or next instruction
	 */
	public SimpleInstruction execute(MethodContext methodContext, SimpleInstruction instruction);
}
