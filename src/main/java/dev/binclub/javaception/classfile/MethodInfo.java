package dev.binclub.javaception.classfile;

import dev.binclub.javaception.type.Type;

import java.util.Objects;

public class MethodInfo {
	public final int access;
	public final String name;
	public final Type[] descriptor;
	public CodeAttribute code;
	
	public MethodInfo(int access, String name, String descriptor) {
		this(access, name, Type.parseMethodDescriptor(descriptor));
	}
	
	public MethodInfo(int access, String name, Type[] descriptor) {
		this.access = access;
		this.name = name;
		this.descriptor = descriptor;
	}
	
	private int _hashcode = 0;
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
