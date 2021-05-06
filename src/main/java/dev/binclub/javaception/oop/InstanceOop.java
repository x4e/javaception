package dev.binclub.javaception.oop;

import dev.binclub.javaception.*;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.*;

public class InstanceOop extends Oop {
	private final VirtualMachine vm;
	private final Klass type;
	public final Object[] fields;
	
	public InstanceOop(VirtualMachine vm, Klass type, int fieldCount) {
		this.vm = vm;
		this.type = type;
		fields = new Object[fieldCount];
	}
	
	/**
	 * Call the default constructor
	 */
	public void construct() {
		construct(new Type[]{PrimitiveType.VOID});
	}
		
	public void construct(Type[] types, Object... args) {
		if (types.length < 1)
			throw new IllegalArgumentException("No return type");
		if (types.length - 1 != args.length) // one less arg because return type
			throw new IllegalArgumentException();
		if (types[types.length-1] != PrimitiveType.VOID)
			throw new IllegalArgumentException("Constructor must return void");
		
		var method = type.findMethod("<init>", types);
		vm.executionEngine.invokeMethodObj(type, this, method, args);
	}
	
	public Klass getKlass() {
		return type;
	}
	
	public static InstanceOop _null() {
		return null;
	}
}
