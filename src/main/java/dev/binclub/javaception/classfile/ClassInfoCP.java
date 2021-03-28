package dev.binclub.javaception.classfile;

public class ClassInfoCP extends CpInitializable<Klass> {

	int nameIndex;
	Object[] constantPool;
	String binaryName;

	public ClassInfoCP(int nameIndex, Object[] constantPool) {
		this.nameIndex = nameIndex;
		this.constantPool = constantPool;
	}

	@Override
	public Klass resolve() {
		if (binaryName.startsWith("[")) {
			// array type
		}
		return null;
	}

	@Override
	public void initialize() {
		binaryName = (String) constantPool[nameIndex - 1];
	}
}
