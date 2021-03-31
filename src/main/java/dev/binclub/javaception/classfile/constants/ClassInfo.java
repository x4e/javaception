package dev.binclub.javaception.classfile.constants;

public class ClassInfo {
	
	public final int nameIndex;
	public String name;
	
	public ClassInfo(int nameIndex) {
		this.nameIndex = nameIndex;
	}
	
	public String getClassName() {
		return name;
	}
	
}
