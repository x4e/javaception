package dev.binclub.javaception.oop;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classfile.MethodInfo;
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
	 * Call the default constructor.
	 * Will throw an exception if it does not exist.
	 */
	public void construct() {
		construct(new Type[]{PrimitiveType.VOID});
	}
	
	/**
	 * Call the constructor with the given descriptor.
	 * 
	 * This should only be done once per instance, and should always be done
	 * before attempting to use the instance.
	 */
	public void construct(Type[] types, Object... args) {
		if (types.length < 1)
			throw new IllegalArgumentException("No return type");
		if (types.length - 1 != args.length) // one less arg because return type
			throw new IllegalArgumentException("Wrong number of parameters");
		if (types[types.length-1] != PrimitiveType.VOID)
			throw new IllegalArgumentException("Constructor must return void");
		
		MethodInfo method = null;
		
		// TODO: Only search for constructors in the direct class, ignoring inherited constructors
		var searchType = type;
		while (method == null && searchType != null) {
			method = searchType.findVirtualMethod("<init>", types);
			searchType = searchType.superKlass;
		}
		
		Objects.requireNonNull(method, "Could not find constructor");
		
		vm.executionEngine.invokeMethodObj(type, this, method, args);
	}
	
	public Klass getKlass() {
		return type;
	}
	
	public static InstanceOop _null() {
		return null;
	}
}
