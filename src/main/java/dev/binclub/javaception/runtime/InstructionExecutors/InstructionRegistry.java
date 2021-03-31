package dev.binclub.javaception.runtime.InstructionExecutors;

import dev.binclub.javaception.runtime.ExecutionEngine;
import dev.binclub.javaception.runtime.InstructionExecutor;

public class InstructionRegistry {
	static boolean initialized = false;

	public static void init() {
		if (initialized) {
			return;
		}
		setRange(0x1a, 0x1d, new ExecutorILoad());
		ExecutionEngine.instructionExecutors[0x60] = new ExecutorIAdd();
		setRange(0xac, 0xb0, new ExecutorXReturn());
		initialized = true;
	}

	public static void setRange(int lowerBound, int upperBound, InstructionExecutor executor) {
		for (int i = lowerBound; i <= upperBound; i++) {
			ExecutionEngine.instructionExecutors[i] = executor;
		}
	}
}
