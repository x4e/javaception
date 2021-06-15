package dev.binclub.javaception.type;

import dev.binclub.javaception.klass.*;

import java.util.Arrays;

public class MethodRef {
	public final Klass owner;
	public final String name;
	public final Type[] descriptor;
	
	public MethodRef(Klass owner, MethodId id) {
		this(owner, id.name, id.types);
	}
	
	public MethodRef(Klass owner, String name, Type[] descriptor) {
		this.owner = owner;
		this.name = name;
		this.descriptor = descriptor;
	}
	
	public MethodId toId() {
		return new MethodId(name, descriptor);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof MethodRef)) return false;
		if (this.hashCode() != other.hashCode()) return false;
		var otherMethod = (MethodRef) other;
		if (!this.owner.equals(otherMethod.owner)) return false;
		if (!this.name.equals(otherMethod.name)) return false;
		return Arrays.equals(this.descriptor, otherMethod.descriptor);
	}
	
	private int _hash;
	@Override
	public int hashCode() {
		int hash = _hash;
		if (hash == 0) {
			hash = Arrays.deepHashCode(new Object[]{owner, name, descriptor});
			_hash = hash;
		}
		return hash;
	}
	
	@Override
	public String toString() {
		return "%s.%s".formatted(owner, toId());
	}
}
