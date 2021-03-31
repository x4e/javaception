package dev.binclub.javaception;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.runtime.ExecutionEngine;
import dev.binclub.javaception.runtime.InstructionExecutors.InstructionRegistry;

public class TestRuntime {
	public static void main(String[] args) throws Throwable {
		InputStream stream = ClassFileParser.class.getClassLoader()
				.getResourceAsStream("dev/binclub/javaception/classfile/ClassFileParser.class");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int available = 0;
		while ((available = stream.available()) != 0) {
			byte[] bytes = new byte[available];
			stream.read(bytes);
			bos.write(bytes);
		}
		InstructionRegistry.init();
		ClassFileParser klassFile = new ClassFileParser(bos.toByteArray());
		for (MethodInfo method : klassFile.methods) {
			if (method.getName(klassFile.constantPool).equals("addTest")) {
				Object result = ExecutionEngine.invokeMethodObj(null, method, 5, 5);
				if ((int) result != 10) {
					System.out.println("jvm is broken ");
				}
			}
		}

	}
}
