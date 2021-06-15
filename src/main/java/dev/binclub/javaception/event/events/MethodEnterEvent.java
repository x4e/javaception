package dev.binclub.javaception.event.events;

import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.runtime.MethodContext;

public class MethodEnterEvent {
	public Klass owner;
	public InstanceOop instance;
	public MethodInfo method;
	public final MethodContext context;
	
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
}
