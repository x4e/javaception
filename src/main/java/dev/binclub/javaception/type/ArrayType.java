package dev.binclub.javaception.type;

import java.util.Objects;

public class ArrayType extends ClassType {
	public final int dimensions;
	public final Type inner;
	
	// Trusted constructor: No argument validation
	ArrayType(int dimensions, Type inner) {
		super(toDescriptor(dimensions, inner));
		this.dimensions = dimensions;
		this.inner = inner;
	}
	
	private static String toDescriptor(int dimensions, Type inner) {
		return "[".repeat(Math.max(0, dimensions)) + inner.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ArrayType arrayType = (ArrayType) o;
		return dimensions == arrayType.dimensions &&
			inner.equals(arrayType.inner);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(dimensions, inner);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
