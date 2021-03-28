package dev.binclub.javaception.classfile;

public abstract class CpInitializable<T> extends CpEntry<T> {
	//called after all the entire constant pool has been resolved
	public abstract void initialize();
}
