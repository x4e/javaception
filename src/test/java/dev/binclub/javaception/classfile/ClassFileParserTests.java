package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.runtime.ExecutionEngine;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassFileParserTests {
	@Test
	public void testItself() throws Throwable {
		InputStream stream = ClassFileParser.class.getClassLoader()
			.getResourceAsStream("dev/binclub/javaception/classfile/ClassFileParser.class");
		Objects.requireNonNull(stream);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int available;
		while ((available = stream.available()) != 0) {
			byte[] bytes = new byte[available];
			if (stream.read(bytes) == -1) {
				break;
			}
			bos.write(bytes);
		}
		
		Klass klassFile = ClassFileParser.parse(bos.toByteArray(), InstanceOop._null());
		for (MethodInfo method : klassFile.methods) {
			if (method.name.equals("addTest")) {
				Object result = ExecutionEngine.invokeMethodObj(null, method, 5, 5);
				assertEquals(result, 10);
			}
		}
	}
}
