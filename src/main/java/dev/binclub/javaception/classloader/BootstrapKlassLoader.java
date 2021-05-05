package dev.binclub.javaception.classloader;

import dev.binclub.javaception.*;
import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.klass.Klass;

import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static dev.binclub.javaception.utils.GenericUtils.sneakyThrow;

public class BootstrapKlassLoader {
	private final VirtualMachine vm;
	private final Map<String, Runnable> pendingCreation = new HashMap<>();
	private final Map<String, Klass> bootstrapClasses = new HashMap<>();
	
	public BootstrapKlassLoader(VirtualMachine vm) {
		this.vm = vm;
		var systemClassPath = ModuleFinder.ofSystem();
		for (ModuleReference modRef : systemClassPath.findAll()) {
			addToClassPath(modRef);
		}
	}
	
	public Klass loadClass(String name) {
		var pending = pendingCreation.get(name);
		if (pending != null) {
			pendingCreation.remove(name);
			pending.run();
		}
		return bootstrapClasses.get(name);
	}
	
	public void addToClassPath(Path... paths) {
		for (ModuleReference modRef : ModuleFinder.of(paths).findAll()) {
			addToClassPath(modRef);
		}
	}
	
	public void addToClassPath(ModuleReference modRef) {
		try {
			var mod = modRef.open();
			for (String name : mod.list().toArray(String[]::new)) {
				if (name.endsWith(".class") && !name.endsWith("module-info.class")) {
					var className = name.substring(0, name.length() - 6);
					pendingCreation.put(className, () -> {
						try {
							var op = mod.read(name);
							if (op.isEmpty()) throw new NullPointerException(className);
							var buf = op.get();
							addToClassPath(className, buf);
							mod.release(buf);
						}
						catch (IOException e) {
							sneakyThrow(e);
						}
					});
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void addToClassPath(String name, ByteBuffer buf) {
		try {
			addToClassPath(ClassFileParser.parse(vm, buf, null));
		} catch (Throwable e) {
			new ClassNotFoundException("Couldn't parse '%s'".formatted(name), e)
				.printStackTrace();
		}
	}
	
	public void addToClassPath(Klass klass) {
		bootstrapClasses.put(klass.name, klass);
	}
}
