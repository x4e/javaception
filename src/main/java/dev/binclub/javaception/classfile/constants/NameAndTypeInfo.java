package dev.binclub.javaception.classfile.constants;

public class NameAndTypeInfo {
	public final int nameIndex, descriptorIndex;
	public String name, description;
	
	public NameAndTypeInfo(int nameIndex, int descriptorIndex) {
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}
	
	public Object resolve(Object[] cp) {
		UtfInfo utf = (UtfInfo) cp[nameIndex - 1];
		if (utf != null) name = utf.get();
		utf = (UtfInfo) cp[nameIndex - 1];
		if (utf != null) description = utf.get();
		return this;
	}
}
