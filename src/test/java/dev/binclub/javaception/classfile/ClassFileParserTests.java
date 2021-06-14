package dev.binclub.javaception.classfile;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.runtime.ExecutionEngine;
import dev.binclub.javaception.type.*;
import dev.binclub.javaception.profiler.Profiler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassFileParserTests {
	@Test
	public void testItself() {
		var vm = new VirtualMachine();
		var klass = vm.systemDictionary.findReferencedClass(null, Type.classType("dev/binclub/javaception/classfile/ClassFileParserTests"));
		var string = vm.systemDictionary.java_lang_String()
			.newInstance();
		string.construct(
			new Type[]{Type.arrayType(1, PrimitiveType.BYTE), PrimitiveType.VOID},
			(Object) "Hello".getBytes()
		);
		
		int testsRan = 0;
		
		assertEquals(
			vm.executionEngine.invokeMethodObj(
				klass,
				null,
				klass.findStaticMethod("testField", Type.parseMethodDescriptor("()I"))
			),
			-5
		);
		assertEquals(
			vm.executionEngine.invokeMethodObj(
				klass,
				null,
				klass.findStaticMethod("addTest", Type.parseMethodDescriptor("(II)I")),
				5,
				5
			),
			10
		);
		assertEquals(
			vm.executionEngine.invokeMethodObj(
				klass,
				null,
				klass.findStaticMethod("testCreate", Type.parseMethodDescriptor("()V"))
			),
			null
		);
		assertEquals(
			vm.executionEngine.invokeMethodObj(
				klass,
				null,
				klass.findStaticMethod("loopTest", Type.parseMethodDescriptor("(I)I")),
				5
			),
			loopTest(5)
		);
		Profiler.printAllProfileData();
		if (testsRan != 4)
			throw new IllegalStateException("Could not execute all methods, only " + testsRan + " found");
	}
	public int nonStaticField;
	private static int field = -1;
	private static int field2 = 0;

	public static int testField() {
		field2 = 5;
		return field * field2;
	}

	public static int addTest(int a, int b) {
		return a + b;
	}
	
	public static void testCreate(){
		new ClassFileParserTests();
	}
	
	public static int loopTest(int start) {
		int total = start;
		for (int i = 0; i < 500000; i++) {
			total += i;
		}
		return total;
	}
}
