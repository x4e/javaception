package dev.binclub.javaception.classfile;

import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.*;

import java.util.Objects;

public class FieldInfo {
	public final int access;
	public final Klass owner;
	public final FieldId id;
	
	public int vindex;
	public Object constantValue;
	private int _hashcode = 0;
	
	public FieldInfo(int access, Klass owner, String name, String descriptor) {
		this(access, owner, name, Type.parseFieldDescriptor(descriptor));
	}
	
	public FieldInfo(int access, Klass owner, String name, Type type) {
		this.access = access;
		this.owner = owner;
		this.id = new FieldId(name, type);
	}
	
	@Override
	public int hashCode() {
		int hash = _hashcode;
		if (hash == 0) {
			hash = Objects.hash(owner, id);
			_hashcode = hash;
		}
		return hash;
	}
}
