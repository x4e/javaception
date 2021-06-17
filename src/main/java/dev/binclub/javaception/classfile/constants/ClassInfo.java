package dev.binclub.javaception.classfile.constants;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classloader.KlassLoader;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.Type;

public class ClassInfo {
	private final VirtualMachine vm;
	public final int nameIndex;
	public String name;
	private Klass klass;
	
	public ClassInfo(VirtualMachine vm, int nameIndex) {
		this.vm = vm;
		this.nameIndex = nameIndex;
	}
	
	public Object resolve(Object[] cp) {
		UtfInfo utf = (UtfInfo) cp[nameIndex - 1];
		if (utf != null) name = utf.get();
		return this;
	}
	
	public Klass getKlass(Klass referencedBy) {
		if (this.klass == null) {
			Type type;
			// The spec does not properly explain whether array names should be
			// permitted in CONSTANT_Class_info structures, however they do appear
			// inside of them in practice.
			if (name.startsWith("[")) {
				type = Type.parseFieldDescriptor(name);
			} else {
				type = Type.classType(name);
			}
			this.klass = vm.systemDictionary.findReferencedClass(referencedBy, type);
		}
		return this.klass;
	}
	
	@Override
	public String toString() {
		return "ClassInfo: %s".formatted(name);
	}
}
