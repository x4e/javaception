package dev.binclub.javaception.runtime;

public class MethodContext {
	Object[] localVariables;
	Object[] stack;
	int stackPointer = 0;
	
	public MethodContext(int maxStack, int maxLocals) {
		localVariables = new Object[maxLocals];
		stack = new Object[maxStack];
	}
	
	public void push(Object obj) {
		stack[stackPointer] = obj;
		stackPointer += 1;
	}
	
	public Object pop() {
		stackPointer -= 1;
		return stack[stackPointer];
	}
	
	public Object peek() {
		return stack[stackPointer - 1];
	}
	
	public void store(int index, Object obj) {
		localVariables[index] = obj;
	}
	
	public Object load(int index) {
		return localVariables[index];
	}
}
