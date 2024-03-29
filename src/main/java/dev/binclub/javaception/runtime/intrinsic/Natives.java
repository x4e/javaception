package dev.binclub.javaception.runtime.intrinsic;

import dev.binclub.javaception.*;
import dev.binclub.javaception.event.*;
import dev.binclub.javaception.event.events.*;
import dev.binclub.javaception.type.*;

import java.util.*;
import java.util.function.*;

public class Natives {
	private final VirtualMachine vm;
	private final HashMap<MethodRef, Consumer<MethodEnterEvent>> handlers;
	
	public Natives(VirtualMachine vm) {
		this.vm = vm;
		this.handlers = new HashMap<>();
		vm.eventSystem.subscribe(MethodEnterEvent.class, this::onMethodEnter);
		
		handlers.put(
			new MethodRef(vm.systemDictionary.java_lang_Class(), "registerNatives", Type.parseMethodDescriptor("()V")), 
			this::noop
		);
		handlers.put(
			new MethodRef(vm.systemDictionary.java_lang_Class(), "desiredAssertionStatus0", Type.parseMethodDescriptor("(Ljava/lang/Class;)Z")), 
			this::noop
		);
	}
	
	public void onMethodEnter(MethodEnterEvent event) {
		var id = event.method.id;
		var ref = new MethodRef(event.owner, id);
		var handler = handlers.get(ref);
		if (handler != null) {
			handler.accept(event);
		}
	}
	
	// NOP method implementation for native methods that shouldnt do anything
	private void noop(MethodEnterEvent event) {
		// no op: force early return
		event.forceEarlyReturn(null);
	}
}
