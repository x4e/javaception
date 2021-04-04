package dev.binclub.javaception.classloader;

import dev.binclub.javaception.classfile.ClassFileParser;
import dev.binclub.javaception.klass.Klass;

import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BootstrapKlassLoader {
	private static final Map<String, Klass> bootstrapClasses = new HashMap<>();
	
	static {
		var systemClassPath = ModuleFinder.ofSystem();
		for (ModuleReference modRef : systemClassPath.findAll()) {
			addToClassPath(modRef);
		}
	}
	
	public static Klass loadClass(String name) {
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
					var entry = mod.read(name);
					if (entry.isPresent()) {
						try {
							addToClassPath(ClassFileParser.parse(entry.get(), null));
						} catch (ClassNotFoundException e) {
							new ClassNotFoundException("Couldn't parse '%s'".formatted(name), e)
								.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void addToClassPath(Klass klass) {
		bootstrapClasses.put(klass.name, klass);
	}
}
