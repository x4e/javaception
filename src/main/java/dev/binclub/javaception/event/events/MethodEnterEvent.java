package dev.binclub.javaception.event.events;

import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.runtime.MethodContext;

public class MethodEnterEvent {
	private Klass owner;
	private InstanceOop instance;
	private MethodInfo method;
	private final MethodContext context;
	
	public MethodEnterEvent(
		Klass owner, 
		InstanceOop instance, 
		MethodInfo method, 
		MethodContext context) {
			this.owner = owner;
			this.instance = instance;
			this.method = method;
			this.context = context;
	}
	
	public Klass getOwner() {
		return this.owner;
	}
	
	public void setOwner(Klass owner) {
		this.owner = owner;
	}
	
	public InstanceOop getInstance() {
		return this.instance;
	}
	
	public void setInstance(InstanceOop instance) {
		this.instance = instance;
	}
	
	public MethodInfo getMethod() {
		return this.method;
	}
	
	public void setMethod(MethodInfo method) {
		this.method = method;
	}
	
	public MethodContext getContext() {
		return this.context;
	}
}
