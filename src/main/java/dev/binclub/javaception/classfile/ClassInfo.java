package dev.binclub.javaception.classfile;

public class ClassInfo {

	int nameIndex;

	public ClassInfo(int nameIndex) {
		this.nameIndex = nameIndex;
	}

	public String getClassName(Object[] cp) {
		return (String) cp[nameIndex - 1];
	}

}
