package dev.binclub.javaception.runtime.intrinsic;

import dev.binclub.javaception.*;
import dev.binclub.javaception.event.*;
import dev.binclub.javaception.event.events.*;

public class Natives {
	private final VirtualMachine vm;
	
	public Natives(VirtualMachine vm) {
		this.vm = vm;
		vm.eventSystem.subscribe(MethodEnterEvent.class, this::onMethodEnter);
	}
	
	public void onMethodEnter(MethodEnterEvent event) {
	
	}
}
