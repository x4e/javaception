package dev.binclub.javaception.classloader;

import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.klass.Klass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static dev.binclub.javaception.utils.GenericUtils.sneakyThrow;

public class BootstrapKlassLoader {
	private static final Map<String, Runnable> pendingCreation = new HashMap<>();
	private static final Map<String, Klass> bootstrapClasses = new HashMap<>();
	
	static {
		var systemClassPath = ModuleFinder.ofSystem();
		for (ModuleReference modRef : systemClassPath.findAll()) {
			addToClassPath(modRef);
		}
	}
	
	public static Klass loadClass(String name) {
		var pending = pendingCreation.get(name);
		if (pending != null) {
			pendingCreation.remove(name);
			pending.run();
		}
		return bootstrapClasses.get(name);
	}
	
	public static void addToClassPath(Path... paths) {
		for (ModuleReference modRef : ModuleFinder.of(paths).findAll()) {
			addToClassPath(modRef);
		}
	}
	
	public static void addToClassPath(ModuleReference modRef) {
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
	
	public static void addToClassPath(String name, ByteBuffer buf) {
		try {
			addToClassPath(ClassFileParser.parse(buf, null));
		} catch (Throwable e) {
			new ClassNotFoundException("Couldn't parse '%s'".formatted(name), e)
				.printStackTrace();
		}
	}
	
	public static void addToClassPath(Klass klass) {
		bootstrapClasses.put(klass.name, klass);
	}
}
