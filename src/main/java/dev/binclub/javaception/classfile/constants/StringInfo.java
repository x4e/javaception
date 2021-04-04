package dev.binclub.javaception.classfile.constants;

import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;

public class StringInfo {
	public final int utfIndex;
	public InstanceOop inner;
	
	public StringInfo(int utfIndex) {
		this.utfIndex = utfIndex;
	}
	
	public InstanceOop resolve(Object[] cp) {
		if (inner == null) {
			UtfInfo utf = (UtfInfo) cp[utfIndex - 1];
			if (utf != null) {
				Klass java_lang_String = SystemDictionary.java_lang_String();
				// TODO
				throw new IllegalStateException("Unimplemented");
			}
		}
		return inner;
	}
}
