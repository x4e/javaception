package dev.binclub.javaception.classfile.constants;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.*;

public abstract class RefInfo<T> {
	private final VirtualMachine vm;
	public final int classIndex, nameAndTypeIndex;
	protected ClassInfo classInfo;
	protected NameAndTypeInfo nameAndType;
	
	protected Klass owner;
	// FieldId or MethodId
	protected T id;
	
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
			this.owner = vm.systemDictionary.findReferencedClass(referencedBy, Type.classType(classInfo.name));
		}
		return this.owner;
	}
	
	public abstract T getId();
	
	public static class FieldRef extends RefInfo<FieldId> {
		public FieldRef(VirtualMachine vm, int classIndex, int nameAndTypeIndex) {
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
	}
	
	public static class MethodRef extends RefInfo<MethodId> {	
		public MethodRef(VirtualMachine vm, int classIndex, int nameAndTypeIndex) {
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
	}
}
