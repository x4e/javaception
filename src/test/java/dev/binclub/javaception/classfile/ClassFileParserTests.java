package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classloader.BootstrapKlassLoader;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.runtime.ExecutionEngine;
import dev.binclub.javaception.type.ClassType;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassFileParserTests {
	@Test
	public void testItself() throws Throwable {
		
		Klass klassFile = SystemDictionary.findReferencedClass(null, new ClassType("dev/binclub/javaception/classfile/ClassFileParserTests"));
		assertNotNull(klassFile);
		for (MethodInfo method : klassFile.methods) {
			if (method.name.equals("testField")) {
				Object result = ExecutionEngine.invokeMethodObj(null, method);
				assertEquals(result, -5);
			}
			if (method.name.equals("addTest")) {
				Object result = ExecutionEngine.invokeMethodObj(null, method, 5, 5);
				assertEquals(result, 10);
			}
			if(method.name.equals("testCreate")){
				ExecutionEngine.invokeMethodObj(null, method);
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
