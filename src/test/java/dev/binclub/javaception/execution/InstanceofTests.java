package dev.binclub.javaception.execution;

import dev.binclub.javaception.VirtualMachine;
import dev.binclub.javaception.type.MethodId;
import dev.binclub.javaception.type.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;

public class InstanceofTests {
	
	@Test
	public void testInstanceof() {
		var vm = new VirtualMachine();
		var klass = vm.systemDictionary.findReferencedClass(null, Type.classType("dev/binclub/javaception/execution/InstanceofTests"));
		var testMethod = klass.findStaticMethod(new MethodId("doTest", Type.parseMethodDescriptor("()Z")));
		Assertions.assertNotEquals(testMethod, null);
		
		vm.executionEngine.invokeMethodObj(klass, null, testMethod);
	}
	
	public static boolean doTest() {
		return InstanceofTests.isSerializable(new byte[]{});
	}
	
	public static boolean isSerializable(Object obj) {
		return obj instanceof byte[];
	}
}
