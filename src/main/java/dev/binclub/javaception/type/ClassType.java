package dev.binclub.javaception.type;

import java.util.Objects;

public class ClassType extends Type {
	/**
	 * Internal (/) name. Does *not* include L; surrounding.
	 * If this is an {@link ArrayType} then it will include [ prefixes to represent the array dimensions.
	 */
	public final String name;
	
	// Trusted constructor: No argument validation
	ClassType(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClassType classType = (ClassType) o;
		return name.equals(classType.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	
	@Override
	public String toString() {
		return "L%s;".formatted(name);
	}
}
