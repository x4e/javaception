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
		assertEquals(Type.parseFieldDescriptor("[B"), Type.arrayType(1, PrimitiveType.BYTE));
		assertEquals(Type.parseFieldDescriptor("[[[[[[[[[Ljava/lang/Object;"), Type.arrayType(9, Type.classType("java/lang/Object")));
	}
	
	@Test
	public void testClassFieldDescriptors() {
		assertEquals(Type.parseFieldDescriptor("Ljava/lang/Object;"), Type.classType("java/lang/Object"));
		assertEquals(Type.parseFieldDescriptor("Ljava.lang.Object;"), Type.classType("java.lang.Object")); // this shouldnt happen in reality
		assertEquals(Type.parseFieldDescriptor("Ljava[lang[BObjectBZIC;"), Type.classType("java[lang[BObjectBZIC"));
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
			Type.parseMethodDescriptor("(Ljava/lang/Object;ZI)V"),
			new Type[]{Type.classType("java/lang/Object"), PrimitiveType.BYTE, PrimitiveType.INT, PrimitiveType.VOID}
		);
		assertArrayEquals(
			Type.parseMethodDescriptor("(Ljava/lang/Object;[[[Z)V"),
			new Type[]{Type.classType("java/lang/Object"), Type.arrayType(3, PrimitiveType.BYTE), PrimitiveType.VOID}
		);
		
		assertEquals(Type.parseMethodReturnType("()V"), PrimitiveType.VOID);
		assertEquals(Type.parseMethodReturnType("()Ljava/lang/Object;"), Type.classType("java/lang/Object"));
	}
	
	@Test
	public void testInvalidMethodDescriptors() {
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodDescriptor("I"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodDescriptor("(I"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodDescriptor("O"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodDescriptor("(O)O"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodDescriptor("(Lhello;LnopeIB)V"));
		
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()O"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()Ljava/lang/Object"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()java/lang/Object;"));
		assertThrows(IllegalArgumentException.class, () -> Type.parseMethodReturnType("()hello"));
	}
}
