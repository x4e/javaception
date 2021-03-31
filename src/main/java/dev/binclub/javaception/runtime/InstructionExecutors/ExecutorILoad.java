package dev.binclub.javaception.runtime.InstructionExecutors;

import dev.binclub.javaception.classfile.instructions.SimpleInstruction;
import dev.binclub.javaception.runtime.InstructionExecutor;
import dev.binclub.javaception.runtime.MethodContext;

public class ExecutorILoad implements InstructionExecutor{

	@Override
	public SimpleInstruction execute(MethodContext methodContext, SimpleInstruction instruction) {
		int index = instruction.getOpcode() - 0x1a;
		methodContext.push(methodContext.load(index));
		return instruction.getNextInstruction();
	}
	
}
