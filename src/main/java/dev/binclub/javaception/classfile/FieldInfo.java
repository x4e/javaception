package dev.binclub.javaception.classfile;

import dev.binclub.javaception.type.FieldId;
import dev.binclub.javaception.type.Type;

import java.util.Objects;

public class FieldInfo {
	public final int access;
	public final String name;
	public final Type type;
	
	public Object constantValue;
	
	public FieldInfo(int access, String name, String descriptor) {
		this(access, name, Type.parseFieldDescriptor(descriptor));
	}
	
	public FieldInfo(int access, String name, Type type) {
		this.access = access;
		this.name = name;
		this.type = type;
	}
	
	private int _hashcode = 0;
	@Override
	public int hashCode() {
		int hash = _hashcode;
		if (hash == 0) {
			hash = Objects.hash(name, type);
			_hashcode = hash;
		}
		return hash;
	}
}
