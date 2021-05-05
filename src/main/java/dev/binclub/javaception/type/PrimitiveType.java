package dev.binclub.javaception.type;

public final class PrimitiveType extends Type {
	public static final PrimitiveType VOID = new PrimitiveType("V", Void.class);
	public static final PrimitiveType BYTE = new PrimitiveType("B", byte.class);
	public static final PrimitiveType SHORT = new PrimitiveType("S", short.class);
	public static final PrimitiveType INT = new PrimitiveType("I", int.class);
	public static final PrimitiveType LONG = new PrimitiveType("J", long.class);
	public static final PrimitiveType CHAR = new PrimitiveType("C", char.class);
	public static final PrimitiveType FLOAT = new PrimitiveType("F", float.class);
	public static final PrimitiveType DOUBLE = new PrimitiveType("D", double.class);
	public static final PrimitiveType BOOLEAN = new PrimitiveType("Z", boolean.class);
	
	private final String name;
	private final int id;
	public final Class<?> jvmClass;
	
	private PrimitiveType(String name, Class<?> jvmClass) {
		this.name = name;
		this.id = name.charAt(0);
		this.jvmClass = jvmClass;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
