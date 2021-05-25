package dev.binclub.javaception.type;

import dev.binclub.javaception.klass.*;

import java.util.Arrays;

public class MethodId {
	public final String name;
	public final Type[] type;
	
	public MethodId(String name, Type[] type) {
		this.owner = owner;
		this.name = name;
		this.type = type;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof MethodId)) return false;
		if (this.hashCode() != other.hashCode()) return false;
		var otherMethod = (MethodId) other;
		if (!this.name.equals(otherMethod.name)) return false;
		return Arrays.equals(this.type, otherMethod.type);
	}
	
	private int _hash;
	@Override
	public int hashCode() {
		int hash = _hash;
		if (hash == 0) {
			hash = Arrays.deepHashCode(new Object[]{name, type});
			_hash = hash;
		}
		return hash;
	}
	
	@Override
	public String toString() {	
		var out = new StringBuilder(name)
			.append('(');
		for (int i = 0; i < descriptor.length; i++) {
			var type = descriptor[i];
			if (i < descriptor.length - 1) {
				out.append(type);
			} else {
				// Last item must have ) first
				out.append(')')
					.append(type);
			}
		}
		return out.toString();
	}
}
