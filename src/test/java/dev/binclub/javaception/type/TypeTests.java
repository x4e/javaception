package dev.binclub.javaception.type;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TypeTests {
	@Test
	public void testPrimitiveFieldDescriptors() {
		assertEquals(Type.parseFieldDescriptor("B"), PrimitiveType.BYTE);
		assertEquals(Type.parseFieldDescriptor("S"), PrimitiveType.SHORT);
		assertEquals(Type.parseFieldDescriptor("I"), PrimitiveType.INT);
		assertEquals(Type.parseFieldDescriptor("J"), PrimitiveType.LONG);
		assertEquals(Type.parseFieldDescriptor("C"), PrimitiveType.CHAR);
		assertEquals(Type.parseFieldDescriptor("F"), PrimitiveType.FLOAT);
		assertEquals(Type.parseFieldDescriptor("D"), PrimitiveType.DOUBLE);
	}
	
	@Test
	public void testArrayFieldDescriptors() {
		assertEquals(Type.parseFieldDescriptor("[B"), new ArrayType(1, PrimitiveType.BYTE));
		assertEquals(Type.parseFieldDescriptor("[[[[[[[[[Ljava/lang/Object;"), new ArrayType(9, new ClassType("java/lang/Object")));
	}
	
	@Test
	public void testClassFieldDescriptors() {
		assertEquals(Type.parseFieldDescriptor("Ljava/lang/Object;"), new ClassType("java/lang/Object"));
		assertEquals(Type.parseFieldDescriptor("Ljava.lang.Object;"), new ClassType("java.lang.Object")); // this shouldnt happen in reality
		assertEquals(Type.parseFieldDescriptor("Ljava[lang[BObjectBZIC;"), new ClassType("java[lang[BObjectBZIC"));
	}
	
	@Test
	public void testInvalidFieldDescriptors() {
		assertThrows(IllegalArgumentException.class, () -> Type.parseFieldDescriptor("Ljava/lang/Object"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseFieldDescriptor("hello"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseFieldDescriptor("(Z)V"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseFieldDescriptor("V"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseFieldDescriptor("java/lang/Object"));
	}
	
	@Test
	public void testMethodDescriptors() {
		assertArrayEquals(
			Type.parseMethodArguments("(Ljava/lang/Object;ZI)V"),
			new Type[]{new ClassType("java/lang/Object"), PrimitiveType.BYTE, PrimitiveType.INT}
		);
		assertArrayEquals(
			Type.parseMethodArguments("(Ljava/lang/Object;[[[Z)V"),
			new Type[]{new ClassType("java/lang/Object"), new ArrayType(3, PrimitiveType.BYTE)}
		);
		
		assertEquals(Type.parseMethodReturnType("()V"), PrimitiveType.VOID);
		assertEquals(Type.parseMethodReturnType("()Ljava/lang/Object;"), new ClassType("java/lang/Object"));
	}
	
	@Test
	public void testInvalidMethodDescriptors() {
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodArguments("I"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodArguments("(I"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodArguments("O"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodArguments("(O)O"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodArguments("(Lhello;LnopeIB)V"));
		
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()O"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()Ljava/lang/Object"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()java/lang/Object;"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()hello"));
	}
}
