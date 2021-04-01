package dev.binclub.javaception.type;

import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.runtime.ExecutionEngine;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassFileParserTests {
	@Test
	public void testItself() throws Throwable {
		InputStream stream = ClassFileParser.class.getClassLoader()
			.getResourceAsStream("dev/binclub/javaception/classfile/ClassFileParser.class");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int available;
		while ((available = stream.available()) != 0) {
			byte[] bytes = new byte[available];
			stream.read(bytes);
			bos.write(bytes);
		}
		ClassFileParser klassFile = new ClassFileParser(bos.toByteArray(), null);
		for (MethodInfo method : klassFile.methods) {
			if (method.name.equals("addTest")) {
				Object result = ExecutionEngine.invokeMethodObj(null, method, 5, 5);
				assertEquals(result, 10);
			}
		}
	}
}
