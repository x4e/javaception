package dev.binclub.javaception.runtime.InstructionExecutors;

import dev.binclub.javaception.classfile.instructions.SimpleInstruction;
import dev.binclub.javaception.runtime.InstructionExecutor;
import dev.binclub.javaception.runtime.MethodContext;

public class ExecutorIAdd implements InstructionExecutor {
	
	@Override
	public SimpleInstruction execute(MethodContext methodContext, SimpleInstruction instruction) {
		int a = (int) methodContext.pop();
		int b = (int) methodContext.pop();
		methodContext.push(a + b);
		return instruction.getNextInstruction();
	}
	
}
