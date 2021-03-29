package dev.binclub.javaception.runtime;

public interface Instruction {
	// return true if execution for this method should finish
	public boolean execute(MethodContext methodContext, int[] instructions, MutableInt instructionPointer);
}
