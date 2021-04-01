package dev.binclub.javaception.classfile.constants;

public class MethodHandleInfo {
	
	public final HandleTypes type;
	public final int referenceIndex;
	public RefInfo refInfo;
	
	public MethodHandleInfo(int referenceKind, int referenceIndex) {
		type = HandleTypes.values()[referenceKind - 1];
		this.referenceIndex = referenceIndex;
	}
	
	public Object resolve(Object[] cp) {
		refInfo = (RefInfo) cp[referenceIndex - 1];
		return this;
	}
	
	public enum HandleTypes {
		GETFIELD, GETSTATIC, PUTFIELD, PUTSTATIC, INVOKEVIRTUAL, INVOKESTATIC, INVOKESPECIAL, NEWINVOKESPECIAL,
		INVOKEINTERFACE
	}
}
