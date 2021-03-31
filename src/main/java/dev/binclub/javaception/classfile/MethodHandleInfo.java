package dev.binclub.javaception.classfile;

public class MethodHandleInfo {
	
	HandleTypes type;
	int referenceIndex;
	
	public MethodHandleInfo(int referenceKind, int referenceIndex) {
		type = HandleTypes.values()[referenceKind - 1];
		this.referenceIndex = referenceIndex;
	}
	
	public RefInfo getReferenceInfo(Object[] cp) {
		return (RefInfo) cp[referenceIndex - 1];
	}
	
	public enum HandleTypes {
		GETFIELD, GETSTATIC, PUTFIELD, PUTSTATIC, INVOKEVIRTUAL, INVOKESTATIC, INVOKESPECIAL, NEWINVOKESPECIAL,
		INVOKEINTERFACE
	}
}
