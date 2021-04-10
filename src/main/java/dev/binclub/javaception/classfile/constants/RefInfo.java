package dev.binclub.javaception.classfile.constants;

import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.ClassType;

public class RefInfo {
	public final int classIndex, nameAndTypeIndex;
	public ClassInfo classInfo;
	public NameAndTypeInfo nameAndTypeInfo;
	public RefType type;
	private Klass owner;
	//index of field or method
	private int id = -1;
	
	public RefInfo(int classIndex, int nameAndTypeIndex, RefType type) {
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
		this.type = type;
	}
	
	public Object resolve(Object[] cp) {
		classInfo = (ClassInfo) cp[classIndex - 1];
		nameAndTypeInfo = (NameAndTypeInfo) cp[nameAndTypeIndex - 1];
		return this;
	}
	
	public Klass getOwner() {
		if (owner == null) {
			this.owner = SystemDictionary.findReferencedClass(null, new ClassType(classInfo.name));
		}
		return this.owner;
	}
	
	public int getID() {
		if (owner == null || id == -1) {
			Klass klazz = SystemDictionary.findReferencedClass(null, new ClassType(classInfo.name));
			this.owner = klazz;
			switch (type) {
			case FIELD -> {
				id = klazz.getFieldID(nameAndTypeInfo.name, nameAndTypeInfo.description);
			}
			case METHOD -> {
				id = klazz.getMethodID(nameAndTypeInfo.name, nameAndTypeInfo.description);
			}
			}
		}
		return id;
	}
	
	public enum RefType {
		FIELD, METHOD, INTERFACE;
	}
}
