package dev.binclub.javaception.type;

import dev.binclub.javaception.klass.*;

import java.util.Objects;

public class FieldId {
	public final String name;
	public final Type type;
	
	public FieldId(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof FieldId)) return false;
		if (this.hashCode() != other.hashCode()) return false;
		var otherField = (FieldId) other;
		if (!this.name.equals(otherField.name)) return false;
		return this.type.equals(otherField.type);
	}
	
	private int _hash;
	@Override
	public int hashCode() {
		int hash = _hash;
		if (hash == 0) {
			hash = Objects.hash(name, type);
			_hash = hash;
		}
		return hash;
	}
}
