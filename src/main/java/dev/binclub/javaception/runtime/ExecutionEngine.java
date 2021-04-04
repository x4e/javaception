package dev.binclub.javaception.runtime;

import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.classfile.CodeAttribute;
import dev.binclub.javaception.oop.InstanceOop;

import java.lang.reflect.Modifier;

public class ExecutionEngine {
	// invokes method expecting a return obj to but put onto the caller stack
	public static Object invokeMethodObj(InstanceOop instance, MethodInfo method, Object... args) {
		CodeAttribute code = method.code;
		MethodContext methodContext = new MethodContext(code.getMaxStack(), code.getMaxLocals());
		int index = 0;
		// reference to self
		if (!Modifier.isStatic(method.access)) {
			methodContext.store(0, instance);
			index += 1;
		}
		// store args into localvariables
		for (Object obj : args) {
			methodContext.store(index, obj);
			if (obj instanceof Long || obj instanceof Double) {
				index += 2;
			}
			else {
				++index;
			}
		}
		/*SimpleInstruction instruction = code.getInstructions().get(0);
		InstructionExecutor executor = instructionExecutors[instruction.getOpcode()];
		if (executor == null) {
			throw new RuntimeException("Unsupported instruction " + String.format("0x%2X", instruction.getOpcode()));
		}
		while ((instruction = executor.execute(methodContext, instruction)) != null) {
			executor = instructionExecutors[instruction.getOpcode()];
		}*/
		return methodContext.pop();
		
	}
}
