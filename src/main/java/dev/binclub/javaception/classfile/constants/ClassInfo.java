package dev.binclub.javaception.classfile.constants;

import dev.binclub.javaception.classloader.KlassLoader;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.Type;

public class ClassInfo {
	public final int nameIndex;
	public String name;
	private Klass klass;
	
	public ClassInfo(int nameIndex) {
		this.nameIndex = nameIndex;
	}
	
	public Object resolve(Object[] cp) {
		UtfInfo utf = (UtfInfo) cp[nameIndex - 1];
		if (utf != null) name = utf.get();
		return this;
	}
	
	public Klass getKlass(Klass referencedBy) {
		if (this.klass == null) {
			if (name.contains("[")) {
				int dimensions = 0;
				for(char letter : name.toCharArray()){
					if(letter == '['){
						dimensions++;
					}
				}
				this.klass = KlassLoader.createArrayClass(referencedBy, null, Type.arrayType(dimensions, Type.classType(name.replace("[",""))));
			}else {
				this.klass = SystemDictionary.findReferencedClass(referencedBy, Type.classType(name));
			}
		}
		return this.klass;
	}
	
	@Override
	public String toString() {
		return "ClassInfo: %s".formatted(name);
	}
}
