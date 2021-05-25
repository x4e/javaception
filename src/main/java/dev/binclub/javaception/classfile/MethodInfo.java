package dev.binclub.javaception.classfile;

import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.*;

import java.util.Objects;

public class MethodInfo {
	public final int access;
	public final Klass owner;
	public final MethodId id;
	public CodeAttribute code;
	public Klass owner;
	private int _hashcode = 0;
	
	public MethodInfo(int access, Klass owner, String name, String descriptor) {
		this(access, owner, name, Type.parseMethodDescriptor(descriptor));
	}
	
	public MethodInfo(int access, Klass owner, String name, Type[] descriptor) {
		this.access = access;
		this.owner = owner;
		this.id = new MethodId(name, descriptor);
		this.name = name;
		this.descriptor = descriptor;
		this.signature = signature;
	}
	
	@Override
	public int hashCode() {
		int hash = _hashcode;
		if (hash == 0) {
			hash = Objects.hash(name, descriptor);
			_hashcode = hash;
		}
		return hash;
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
}
