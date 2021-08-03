package dev.binclub.javaception.oop;

import dev.binclub.javaception.*;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.type.*;

public abstract class ArrayOop<T> extends InstanceOop {
	public ArrayOop(VirtualMachine vm, Klass referencedBy, ArrayType type) {
		super(
			vm,
			vm.systemDictionary.findReferencedClass(referencedBy, type),
			/* no fields */ 0
		);
	}
	
	public abstract T get(int index);
	
	public abstract int length();
	
	public static class Boolean extends ArrayOop<java.lang.Boolean> {
		private final boolean[] arr;
		
		public Boolean(VirtualMachine vm, Klass referencedBy, int length) {
			super(vm, referencedBy, Type.arrayType(1, PrimitiveType.BOOLEAN));
			this.arr = new boolean[length];
		}
		
		@Override
		public java.lang.Boolean get(int index) {
			return this.arr[index];
		}
		
		@Override
		public int length() {
			return this.arr.length;
		}
	}
	
	public static class Char extends ArrayOop<java.lang.Character> {
		private final char[] arr;
		
		public Char(VirtualMachine vm, Klass referencedBy, int length) {
			super(vm, referencedBy, Type.arrayType(1, PrimitiveType.CHAR));
			this.arr = new char[length];
		}
		
		@Override
		public java.lang.Character get(int index) {
			return this.arr[index];
		}
		
		@Override
		public int length() {
			return this.arr.length;
		}
	}
	
	public static class Float extends ArrayOop<java.lang.Float> {
		private final float[] arr;
		
		public Float(VirtualMachine vm, Klass referencedBy, int length) {
			super(vm, referencedBy, Type.arrayType(1, PrimitiveType.FLOAT));
			this.arr = new float[length];
		}
		
		@Override
		public java.lang.Float get(int index) {
			return this.arr[index];
		}
		
		@Override
		public int length() {
			return this.arr.length;
		}
	}
	
	public static class Double extends ArrayOop<java.lang.Double> {
		private final double[] arr;
		
		public Double(VirtualMachine vm, Klass referencedBy, int length) {
			super(vm, referencedBy, Type.arrayType(1, PrimitiveType.DOUBLE));
			this.arr = new double[length];
		}
		
		@Override
		public java.lang.Double get(int index) {
			return this.arr[index];
		}
		
		@Override
		public int length() {
			return this.arr.length;
		}
	}
	
	public static class Byte extends ArrayOop<java.lang.Byte> {
		private final byte[] arr;
		
		public Byte(VirtualMachine vm, Klass referencedBy, int length) {
			super(vm, referencedBy, Type.arrayType(1, PrimitiveType.BYTE));
			this.arr = new byte[length];
		}
		
		@Override
		public java.lang.Byte get(int index) {
			return this.arr[index];
		}
		
		@Override
		public int length() {
			return this.arr.length;
		}
	}
	
	public static class Short extends ArrayOop<java.lang.Short> {
		private final short[] arr;
		
		public Short(VirtualMachine vm, Klass referencedBy, int length) {
			super(vm, referencedBy, Type.arrayType(1, PrimitiveType.SHORT));
			this.arr = new short[length];
		}
		
		@Override
		public java.lang.Short get(int index) {
			return this.arr[index];
		}
		
		@Override
		public int length() {
			return this.arr.length;
		}
	}
	
	public static class Integer extends ArrayOop<java.lang.Integer> {
		private final int[] arr;
		
		public Integer(VirtualMachine vm, Klass referencedBy, int length) {
			super(vm, referencedBy, Type.arrayType(1, PrimitiveType.INT));
			this.arr = new int[length];
		}
		
		@Override
		public java.lang.Integer get(int index) {
			return this.arr[index];
		}
		
		@Override
		public int length() {
			return this.arr.length;
		}
	}
	
	public static class Long extends ArrayOop<java.lang.Long> {
		private final long[] arr;
		
		public Long(VirtualMachine vm, Klass referencedBy, int length) {
			super(vm, referencedBy, Type.arrayType(1, PrimitiveType.LONG));
			this.arr = new long[length];
		}
		
		@Override
		public java.lang.Long get(int index) {
			return this.arr[index];
		}
		
		@Override
		public int length() {
			return this.arr.length;
		}
	}
}
