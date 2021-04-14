package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.runtime.ExecutionEngine;
import dev.binclub.javaception.type.PrimitiveType;
import dev.binclub.javaception.type.Type;
import org.junit.jupiter.api.Test;
import profiler.Profiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassFileParserTests {
	@Test
	public void testItself() {
		Klass klass = SystemDictionary.findReferencedClass(null, Type.classType("dev/binclub/javaception/classfile/ClassFileParserTests"));
		Klass stringKlass = SystemDictionary.java_lang_String();
		MethodInfo init = stringKlass.findMethod("<init>",new Type[]{PrimitiveType.VOID});
		ExecutionEngine.invokeMethodObj(stringKlass,stringKlass.newInstance(),init);
		assertNotNull(init);
		assertNotNull(klass);
		int testsRan = 0;
		for (MethodInfo method : klass.methods) {
			if (method.name.equals("testField")) {
				Object result = ExecutionEngine.invokeMethodObj(klass, null, method);
				assertEquals(result, -5);
				testsRan += 1;
			} else if (method.name.equals("addTest")) {
				Object result = ExecutionEngine.invokeMethodObj(klass, null, method, 5, 5);
				assertEquals(result, 10);
				testsRan += 1;
			} else if (method.name.equals("testCreate")){
				ExecutionEngine.invokeMethodObj(klass, null, method);
				testsRan += 1;
			} else if (method.name.equals("loopTest")) {
				int result = (int) ExecutionEngine.invokeMethodObj(klass, null, method, 5);
				int expected = loopTest(5);
				testsRan += 1;
				assertEquals(result, expected);
			}
		}
		ExecutionEngine.printAllProfileData();
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
