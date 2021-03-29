package dev.binclub.javaception.runtime;

import dev.binclub.javaception.classfile.Klass;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.classfile.OpcodeStride;
import dev.binclub.javaception.classfile.attributes.CodeAttribute;
import dev.binclub.javaception.oop.InstanceOop;

public class ExecutionEngine {

	public static Instruction[] instructionHandlers = new Instruction[256];

	// invokes method expecting a return obj
	public static void invokeMethodObj(InstanceOop instance, MethodInfo method, MethodContext caller, Object... args)
			throws Throwable {
		CodeAttribute code = method.getCodeAttribute();
		MethodContext methodContext = new MethodContext(code.getMaxStack(), code.getMaxLocals());
		int index = 0;
		// reference to self
		methodContext.store(0, instance);
		index += 1;
		// store args into localvariables
		for (Object obj : args) {
			methodContext.store(index, obj);
			if (obj instanceof Long || obj instanceof Double) {
				index += 2;
			} else {
				++index;
			}
		}
		int[] instructions = code.getCode();
		MutableInt instructionPointer = new MutableInt();
		for (;;) {
			int byt = instructions[instructionPointer.value];
			Instruction instructionHandle = instructionHandlers[byt];
			if (instructionHandle == null) {
				throw new Throwable("Unsupported Instruction");
			}
			if (instructionHandle.execute(methodContext, instructions, instructionPointer)) {
				caller.push(methodContext.pop());
				return;
			}
			//perhaps make a cache for this?
			int stride = OpcodeStride.getStrideAmount(byt, instructionPointer.value, instructions);
			instructionPointer.value += stride + 1;

		}

	}

	public static void invokeStaticMethod(Klass klass, MethodInfo method, Object... args) {

	}
}
