package dev.binclub.javaception.type;

import java.util.Objects;

public class FieldId {
	public final String name;
	public final Type type;
	
	public FieldId(String name, Type type) {
		this.name = name;
		this.type = type;
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
