package dev.binclub.javaception.klass;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classfile.ClassFileConstants;
import dev.binclub.javaception.classfile.FieldInfo;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.classloader.SystemDictionary;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.runtime.ExecutionEngine;
import dev.binclub.javaception.type.*;

import java.util.*;

public class Klass {
	protected final VirtualMachine vm;
	/**
	 * The defining loader of this class.
	 * Not necessarily the same as the initiating loader.
	 * See JVMS 5.3.
	 */
	public final InstanceOop classLoader;
	public final String name;
	/**
	 * Super Class - maybe null
	 */
	public final Klass superKlass;
	public final Klass[] interfaces;
	/**
	 * Valid constant types:
	 * - Class
	 * - FieldRef
	 * - MethodRef
	 * - InterfaceMethodRef
	 * - MethodHandle
	 * - MethodType
	 * - Dynamic
	 * - InvokeDynamic
	 * - String, int, long, float, double
	 */
	public final Object[] runtimeConstantPool;
	
	/**
	 * Static members which are accessed directly through the klass rather than
	 * an instance. These are not inherited at all from the superklass.
	 */
	public FieldInfo[] staticFields;
	public MethodInfo[] staticMethods;
	
	/**
	 * V-Tables. Contains the virtual methods and fields of this klass.
  	 * These are inherited from the super klass.
	 */
	public FieldInfo[] virtualFields;
	public MethodInfo[] virtualMethods;
	
	public Object[] staticFieldValues;
	public boolean resolved = false;
	
	public Klass(
		VirtualMachine vm,
		InstanceOop classLoader,
		Object[] runtimeConstantPool,
		String name,
		Klass superKlass,
		Klass[] interfaces
	) {
		this.vm = vm;
		this.classLoader = classLoader;
		this.runtimeConstantPool = runtimeConstantPool;
		this.name = name;
		this.superKlass = superKlass;
		this.interfaces = interfaces;
	}
		
	/**
	 * Set the Klass' methods.
	 * This should only be called by the classfile parser.
	 */
	public void setMethods(MethodInfo[] methods) {
		if (this.virtualMethods != null)
			throw new IllegalStateException("Cannot redeclare methods");
		
		Map<MethodId, MethodInfo> virtualMethods = new LinkedHashMap<>(
			(superKlass == null ? 0 : superKlass.virtualMethods.length) + 10
		);
		List<MethodInfo> staticMethods = new ArrayList<>();
		
		for (MethodInfo method : methods) {
			if ((method.access & ClassFileConstants.ACC_STATIC) == 0) {
				virtualMethods.put(method.id, method);
			} else {
				staticMethods.add(method);
			}
		}
		
		this.virtualMethods = virtualMethods.values().toArray(new MethodInfo[0]);
		this.staticMethods = staticMethods.toArray(new MethodInfo[0]);
		
		if (superKlass != null) {
			this.virtualMethods = Arrays.copyOf(
				superKlass.virtualMethods,
				superKlass.virtualMethods.length + methods.length
			);
			for (int i = superKlass.virtualMethods.length; i < methods.length; i++) {
				this.virtualMethods[i] = methods[i];
			}
		} else {
			this.virtualMethods = methods;
		}
	}
	
	/**
	 * Set the Klass' fields.
	 * This should only be called by the classfile parser.
	 */
	public void setFields(FieldInfo[] fields) {
		if (this.virtualFields != null)
			throw new IllegalStateException("Cannot redeclare fields");
		
		var virtualFields = new ArrayList<>();
		var staticFields = new ArrayList<>();
		
		for (FieldInfo field : fields) {
			if ((field.access & ClassFileConstants.ACC_STATIC) == 0) {
				virtualFields.add(field);
			} else {
				staticFields.add(field);
			}
		}
				
		fields = virtualFields.toArray(new FieldInfo[0]);
		this.staticFields = staticFields.toArray(new FieldInfo[0]);
		staticFieldValues = new Object[this.staticFields.length];
		
		if (superKlass != null) {
			FieldInfo[] superFields = superKlass.virtualFields;
			this.virtualFields = Arrays.copyOf(
				superFields,
				superFields.length + fields.length
			);
			for (int i = superFields.length; i < this.virtualFields.length; i++) {
				var field = fields[i];
				field.vindex = i;
				this.virtualFields[i] = field;
			}
		} else {
			this.virtualFields = fields;
		}
	}
	
	public FieldInfo findStaticField(String name, Type type) {
		return findStaticField(new FieldId(name, type));
	}
	
	public FieldInfo findStaticField(FieldId id) {
		for (FieldInfo field : this.staticFields) {
			if (id.equals(field.id)) {
				return field;
			}
		}
		return null;
	}
	
	public FieldInfo findVirtualField(String name, Type type) {
		return findVirtualField(new FieldId(name, type));
	}
	
	public FieldInfo findVirtualField(FieldId id) {
		for (FieldInfo field : this.virtualFields) {
			if (id.equals(field.id)) {
				return field;
			}
		}
		return null;
	}
	
	public MethodInfo findStaticMethod(String name, Type[] types) {
		return findStaticMethod(new MethodId(name, types));
	}
	
	public MethodInfo findStaticMethod(MethodId id) {
		for (MethodInfo method : this.staticMethods) {
			if (id.equals(method.id)) {
				return method;
			}
		}
		return null;
	}
	
	public MethodInfo findVirtualMethod(String name, Type[] types) {
		return findVirtualMethod(new MethodId(name, types));
	}
	
	public MethodInfo findVirtualMethod(MethodId id) {
		for (MethodInfo method : this.virtualMethods) {
			if (id.equals(method.id)) {
				return method;
			}
		}
		return null;
	}

	public void resolve() {
		resolved = true;
		var clinit = findStaticMethod("<clinit>", new Type[]{PrimitiveType.VOID});
		if (clinit != null) {
			vm.executionEngine.invokeMethodObj(this, null, clinit);
		}
	}
	
	public InstanceOop newInstance() {
		return new InstanceOop(vm, this, this.virtualFields.length);
	}
	
	InstanceOop asKlass;
	static int nameId = -1;
	
	public InstanceOop asJavaLangClass(){
		if (asKlass == null) {
			asKlass = vm.systemDictionary.java_lang_Class().newInstance();
			asKlass.construct();
			if(nameId == -1){
				var field = vm.systemDictionary.java_lang_Class().findVirtualField(
					"name" , 
					Type.classType(vm.systemDictionary.java_lang_String().name)
				);
				nameId = field.vindex;
				asKlass.fields[nameId] = name;
			}
		}
		return asKlass;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
