package dev.binclub.javaception.classfile.constants;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.*;

public abstract class RefInfo<A, B> {
	private final VirtualMachine vm;
	private final int classIndex, nameAndTypeIndex;
	protected ClassInfo classInfo;
	protected NameAndTypeInfo nameAndType;
	
	protected Klass owner;
	protected A id;
	protected B ref;
	
	RefInfo(VirtualMachine vm, int classIndex, int nameAndTypeIndex) {
		this.vm = vm;
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}
	
	public Object resolve(Object[] cp) {
		classInfo = (ClassInfo) cp[classIndex - 1];
		nameAndType = (NameAndTypeInfo) cp[nameAndTypeIndex - 1];
		return this;
	}
	
	public Klass getOwner(Klass referencedBy) {
		if (owner == null) {
			this.owner = classInfo.getKlass(referencedBy);
		}
		return this.owner;
	}
		
	public abstract A getId();
	public abstract B getRef(Klass referencedBy);
	
	public static class Field extends RefInfo<FieldId, FieldRef> {
		public Field(VirtualMachine vm, int classIndex, int nameAndTypeIndex) {
			super(vm, classIndex, nameAndTypeIndex);
		}
		
		@Override
		public FieldId getId() {
			if (id == null) {
				id = new FieldId(
					nameAndType.name,
					Type.parseFieldDescriptor(nameAndType.descriptor)
				);
			}
			return id;
		}
		
		@Override
		public FieldRef getRef(Klass referencedBy) {
			if (ref == null) {
				ref = new FieldRef(
					getOwner(referencedBy),
					nameAndType.name,
					Type.parseFieldDescriptor(nameAndType.descriptor)
				);
			}
			return ref;
		}
	}
	
	public static class Method extends RefInfo<MethodId, MethodRef> {
		public Method(VirtualMachine vm, int classIndex, int nameAndTypeIndex) {
			super(vm, classIndex, nameAndTypeIndex);
		}
				
		@Override
		public MethodId getId() {
			if (id == null) {
				id = new MethodId(
					nameAndType.name,
					Type.parseMethodDescriptor(nameAndType.descriptor)
				);
			}
			return id;
		}
		
		@Override
		public MethodRef getRef(Klass referencedBy) {
			if (ref == null) {
				ref = new MethodRef(
					getOwner(referencedBy),
					nameAndType.name, 
					Type.parseMethodDescriptor(nameAndType.descriptor)
				);
			}
			return ref;
		}
	}
}
