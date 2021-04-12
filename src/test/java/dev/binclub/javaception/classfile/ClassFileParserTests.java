package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.runtime.ExecutionEngine;
import dev.binclub.javaception.type.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassFileParserTests {
	@Test
	public void testItself() {
		Klass klass = SystemDictionary.findReferencedClass(null, Type.classType("dev/binclub/javaception/classfile/ClassFileParserTests"));
		assertNotNull(klass);
		for (MethodInfo method : klass.methods) {
			if (method.name.equals("testField")) {
				Object result = ExecutionEngine.invokeMethodObj(klass, null, method);
				assertEquals(result, -5);
			} else if (method.name.equals("addTest")) {
				Object result = ExecutionEngine.invokeMethodObj(klass, null, method, 5, 5);
				assertEquals(result, 10);
			} else if (method.name.equals("testCreate")){
				ExecutionEngine.invokeMethodObj(klass, null, method);
			}
		}
		//throw new IllegalStateException("No method found");
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
}
