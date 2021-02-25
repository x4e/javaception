package dev.binclub.javaception.type;

public final class PrimitiveType extends Type {
	public static final PrimitiveType VOID = new PrimitiveType("V");
	public static final PrimitiveType BYTE = new PrimitiveType("B");
	public static final PrimitiveType SHORT = new PrimitiveType("S");
	public static final PrimitiveType INT = new PrimitiveType("I");
	public static final PrimitiveType LONG = new PrimitiveType("J");
	public static final PrimitiveType CHAR = new PrimitiveType("C");
	public static final PrimitiveType FLOAT = new PrimitiveType("F");
	public static final PrimitiveType DOUBLE = new PrimitiveType("D");
	
	private final String name;
	private final int id;
	
	private PrimitiveType(String name) {
		this.name = name;
		this.id = name.charAt(0);
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
