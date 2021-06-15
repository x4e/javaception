package dev.binclub.javaception.type;

import dev.binclub.javaception.klass.*;

import java.util.Objects;

public class FieldRef {
	public final Klass owner;
	public final String name;
	public final Type descriptor;
	
	public FieldRef(Klass owner, FieldId id) {
		this(owner, id.name, id.type);
	}
	
	public FieldRef(Klass owner, String name, Type descriptor) {
		this.owner = owner;
		this.name = name;
		this.descriptor = descriptor;
	}
	
	public FieldId toId() {
		return new FieldId(name, descriptor);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof FieldRef)) return false;
		if (this.hashCode() != other.hashCode()) return false;
		var otherField = (FieldRef) other;
		if (!this.owner.equals(otherField.owner)) return false;
		if (!this.name.equals(otherField.name)) return false;
		return this.descriptor.equals(otherField.descriptor);
	}
	
	private int _hash;
	@Override
	public int hashCode() {
		int hash = _hash;
		if (hash == 0) {
			hash = Objects.hash(owner, name, descriptor);
			_hash = hash;
		}
		return hash;
	}
	
	@Override
	public String toString() {
		return "%s.%s".formatted(owner, toId());
	}
}
