package dev.binclub.javaception.classfile;

public class RuntimeConstantPool {
	private CpEntry<?>[] cp;
	
	public RuntimeConstantPool(CpEntry<?>[] cp) {
		this.cp = cp;
	}
}
