package dev.binclub.javaception.classfile;

public class RuntimeConstantPool {
	private CpEntry<?>[] cp;
	
	public RuntimeConstantPool(CpEntry<?>[] cp) {
		this.cp = cp;
	}
	
	private abstract static class CpEntry<T> {
		public abstract T resolve();
	}
	
	private static class ClassInfo extends CpEntry<Klass> {
		String binaryName;
		public ClassInfo(String binaryName) {
			this.binaryName = binaryName;
			if (binaryName.isEmpty()) {
				throw new IllegalArgumentException("Invalid Class constant");
			}
		}
		@Override
		public Klass resolve() {
			if (binaryName.startsWith("[")) {
				// array type
			}
			return null;
		}
	}
}
