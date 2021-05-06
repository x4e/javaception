package dev.binclub.javaception.runtime.intrinsic;

import dev.binclub.javaception.*;
import dev.binclub.javaception.event.*;
import dev.binclub.javaception.event.events.*;
import dev.binclub.javaception.type.*;

import java.util.*;
import java.util.function.*;

public class Natives {
	private final VirtualMachine vm;
	private final HashMap<MethodId, Consumer<MethodEnterEvent>> handlers;
	
	public Natives(VirtualMachine vm) {
		this.vm = vm;
		this.handlers = new HashMap<>();
		vm.eventSystem.subscribe(MethodEnterEvent.class, this::onMethodEnter);
	}
	
	public void onMethodEnter(MethodEnterEvent event) {
	
	}
}
