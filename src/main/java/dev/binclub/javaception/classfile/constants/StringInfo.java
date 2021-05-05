package dev.binclub.javaception.classfile.constants;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.*;
import dev.binclub.javaception.oop.InstanceOop;

public class StringInfo {
	private final VirtualMachine vm;
	public final int utfIndex;
	public InstanceOop inner;
	
	public StringInfo(VirtualMachine vm, int utfIndex) {
		this.vm = vm;
		this.utfIndex = utfIndex;
	}
	
	public synchronized InstanceOop resolve(Object[] cp) {
		if (inner == null) {
			UtfInfo utf = (UtfInfo) cp[utfIndex - 1];
			if (utf != null) {
				Klass klass = vm.systemDictionary.java_lang_String();
				byte[] data = utf.data;
				
				inner = klass.newInstance();
				inner.construct(
					new Type[]{Type.arrayType(1, PrimitiveType.BYTE), PrimitiveType.VOID},
					data
				);
			}
		}
		return inner;
	}
}
