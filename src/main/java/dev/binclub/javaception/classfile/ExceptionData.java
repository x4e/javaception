package dev.binclub.javaception.classfile;

public class ExceptionData {
	int startPc;
	int endPc;
	int handlerPc;
	int catchType;

	public ExceptionData(int startPc, int endPc, int handlerPc, int catchType) {
		super();
		this.startPc = startPc;
		this.endPc = endPc;
		this.handlerPc = handlerPc;
		this.catchType = catchType;
	}

}
