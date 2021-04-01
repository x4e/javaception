package dev.binclub.javaception.classfile.constants;

public class StringInfo {
	public final int stringIndex;
	public String inner;
	
	public StringInfo(int stringIndex) {
		this.stringIndex = stringIndex;
	}
	
	public Object resolve(Object[] cp) {
		UtfInfo utf = (UtfInfo) cp[stringIndex - 1];
		if (utf != null) inner = utf.get();
		return this;
	}
}
