package dev.binclub.javaception.type;

import java.util.Objects;

public class ArrayType extends Type {
	public final int dimensions;
	public final Type inner;
	
	public ArrayType(int dimensions, Type inner) {
		if (dimensions < 1) throw new IllegalArgumentException("Dimensions " + dimensions);
		this.dimensions = dimensions;
		this.inner = inner;
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
		StringBuilder sb = new StringBuilder(dimensions + 5);
		for (int i = 0; i < dimensions; i++) {
			sb.append('[');
		}
		sb.append(inner.toString());
		return sb.toString();
	}
}
