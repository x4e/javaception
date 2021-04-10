package dev.binclub.javaception.classfile;

import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.Type;

import java.util.Objects;

public class MethodInfo {
	public final int access;
	public final String name;
	public final Type[] descriptor;
	public final String signature;
	public CodeAttribute code;
	public Klass owner;
	private int _hashcode = 0;
	
	public MethodInfo(int access, String name, String descriptor) {
		this(access, name, Type.parseMethodDescriptor(descriptor), descriptor);
	}
	
	public MethodInfo(int access, String name, Type[] descriptor, String signature) {
		this.access = access;
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
