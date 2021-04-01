package dev.binclub.javaception.classfile.constants;

public class ClassInfo {
	public final int nameIndex;
	public String name;
	
	public ClassInfo(int nameIndex) {
		this.nameIndex = nameIndex;
	}
	
	public Object resolve(Object[] cp) {
		UtfInfo utf = (UtfInfo) cp[nameIndex - 1];
		if (utf != null) name = utf.get();
		return this;
	}
	
	@Override
	public String toString() {
		return "ClassInfo: %s".formatted(name);
	}
}
