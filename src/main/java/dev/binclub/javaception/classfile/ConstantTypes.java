package dev.binclub.javaception.classfile;

public enum ConstantTypes {
	CLASS(7), FIELDREF(9), METHODREF(10), INTERFACEMETHODREF(11), STRING(8), INTEGER(3), FLOAT(4), LONG(5), DOUBLE(6),
	NAMEANDTYPE(12), UTF8(1), METHODHANDLE(15), METHODTYPE(16), INVOKEDYNAMIC(18);

	int value;

	ConstantTypes(int value) {
		ClassFileParser.types[value] = this;
	}
}
