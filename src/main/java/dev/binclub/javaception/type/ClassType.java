package dev.binclub.javaception.type;

import java.util.Objects;

public class ClassType extends Type {
	/**
	 * Internal (/) name. Does *not* include L; surrounding.
	 */
	public final String name;
	
	public ClassType(String name) {
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
		return "L" + name + ";";
	}
}
