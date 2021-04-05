package dev.binclub.javaception.classfile;

import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.runtime.ExecutionEngine;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassFileParserTests {
	@Test
	public void testItself() throws Throwable {
		InputStream stream = ClassFileParser.class.getClassLoader()
			.getResourceAsStream("dev/binclub/javaception/classfile/ClassFileParserTests.class");
		Objects.requireNonNull(stream);
		
		Klass klassFile = ClassFileParser.parse(stream, InstanceOop._null());
		for (MethodInfo method : klassFile.methods) {
			if (method.name.equals("testField")) {
				Object result = ExecutionEngine.invokeMethodObj(null, method);
				assertEquals(result, -5);
				return;
			}
		}
		throw new IllegalStateException("No method found");
	}
	
	private static int field = -1;
	private static int field2 = 0;
	public static int testField() {
		field2 = 5;
		return field * field2;
	}
}
