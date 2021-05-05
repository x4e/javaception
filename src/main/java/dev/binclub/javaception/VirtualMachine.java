package dev.binclub.javaception;

import dev.binclub.javaception.event.EventSystem;
import dev.binclub.javaception.runtime.*;
import dev.binclub.javaception.runtime.intrinsic.*;
import dev.binclub.javaception.classloader.*;

public class VirtualMachine {
	public final EventSystem eventSystem;
	public final ExecutionEngine executionEngine;
	public final BootstrapKlassLoader bootstrapKlassLoader;
	public final KlassLoader klassLoader;
	public final SystemDictionary systemDictionary;
	public final Natives natives;
	
	public VirtualMachine() {
		this.eventSystem = new EventSystem();
		this.executionEngine = new ExecutionEngine(this);
		this.bootstrapKlassLoader = new BootstrapKlassLoader(this);
		this.klassLoader = new KlassLoader(this);
		this.systemDictionary = new SystemDictionary(this);
		this.natives = new Natives(this);
	}
}
