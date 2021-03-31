package dev.binclub.javaception.classfile;

public class FieldRefInfo extends CpInitializable<Field> {
	
	int classIndex;
	int nameTypeIndex;
	
	public FieldRefInfo(int classIndex, int nameTypeIndex) {
		this.classIndex = classIndex;
		this.nameTypeIndex = nameTypeIndex;
	}
	
	@Override
	public void initialize() {
		
	}
	
	@Override
	public Field resolve() {
		return null;
	}
	
}
