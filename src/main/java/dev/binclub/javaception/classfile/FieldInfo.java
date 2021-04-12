package dev.binclub.javaception.classfile;

import dev.binclub.javaception.type.Type;

import java.util.Objects;

public class FieldInfo {
	public final int access;
	public final String name;
	public final Type type;
	public final String descriptor;
	
	public Object constantValue;
	private int _hashcode = 0;
	
	public FieldInfo(int access, String name, String descriptor) {
		this(access, name, Type.parseFieldDescriptor(descriptor), descriptor);
	}
	
	public FieldInfo(int access, String name, Type type, String descriptor) {
		this.access = access;
		this.name = name;
		this.type = type;
		this.descriptor = descriptor;
	}
	
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
