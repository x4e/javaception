package dev.binclub.javaception.event.events;

import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.runtime.MethodContext;

import java.util.Optional;

public class MethodEnterEvent {
	public Klass owner;
	public InstanceOop instance;
	public MethodInfo method;
	public final MethodContext context;
	/**
	 * Can be used to force an early return.
	 * If the field is simply null then that signifies null should be returned early.
	 */
	public Optional<Object> returnValue;
	
	public MethodEnterEvent(
		Klass owner, 
		InstanceOop instance, 
		MethodInfo method, 
		MethodContext context) {
			this.owner = owner;
			this.instance = instance;
			this.method = method;
			this.context = context;
			this.returnValue = Optional.empty();
	}
	
	public void forceEarlyReturn(Object retVal) {
		if (retVal == null) {
			returnValue = null;
		} else {
			returnValue = Optional.of(retVal);
		}
	}
}
