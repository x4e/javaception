package dev.binclub.javaception.classfile;

public class RuntimeConstantPool {
	private byte[] cp;
	private int offset;
	private int items;
	
	public RuntimeConstantPool(byte[] cp, int offset, int items) {
		this.cp = cp;
		this.offset = offset;
		this.items = items;
	}
	
	private abstract static class CpEntry<T> {
		public abstract T resolve();
	}
	
	private static class ClassInfo extends CpEntry<Class> {
		String binaryName;
		public ClassInfo(String binaryName) {
			this.binaryName = binaryName;
			if (binaryName.isEmpty()) {
				throw new IllegalArgumentException("Invalid Class constant");
			}
		}
		@Override
		public Class resolve() {
			if (binaryName.startsWith("[")) {
				// array type
			}
			return null;
		}
	}
}
