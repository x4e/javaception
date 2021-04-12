package dev.binclub.javaception.type;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Type {
	public static ClassType classType(String className) {
		Objects.requireNonNull(className, "className");
		if (className.length() < 1 || className.charAt(className.length() - 1) == ';') {
			throw new IllegalArgumentException(className);
		}
		return new ClassType(className);
	}
	
	public static ArrayType arrayType(int dimensions, Type inner) {
		Objects.requireNonNull(inner, "inner");
		if (dimensions < 1)
			throw new IllegalArgumentException("Dimensions " + dimensions + " is less than 1");
		if (inner instanceof ArrayType)
			throw new IllegalArgumentException("Circular array (attempted to have inner element " + inner + ")");
		return new ArrayType(dimensions, inner);
	}
	
	/**
	 * Parse a field descriptor according to JVMS 4.3.2.
	 * This does not support parameters or void.
	 *
	 * @param descriptor a field descriptor
	 * @return the singular type represented by the descriptor
	 * @throws IllegalArgumentException if the descriptor is invalid
	 */
	public static Type parseFieldDescriptor(String descriptor) {
		char[] chars = descriptor.toCharArray();
		try {
			Type out = parseSingleType(chars, 0, new int[1]);
			
			if (
				out == PrimitiveType.VOID
					||
					(out instanceof ArrayType && ((ArrayType) out).inner == PrimitiveType.VOID)
			) throw new IllegalArgumentException("Field descriptors may not use Void");
			
			return out;
		}
		catch (RuntimeException ex) {
			if (!(ex instanceof IllegalArgumentException)) {
				ex = new IllegalArgumentException("Invalid descriptor '" + descriptor + "'", ex);
			}
			throw ex;
		}
	}
	
	/**
	 * Parses the arguments of a method descriptor.
	 *
	 * @param descriptor method descriptor
	 * @return array of arguments
	 * @throws IllegalArgumentException if the descriptor is invalid
	 */
	public static Type[] parseMethodDescriptor(String descriptor) {
		try {
			char[] chars = descriptor.toCharArray();
			if (chars[0] != '(') throw new IllegalArgumentException("Method descriptor should start with '('");
			ArrayList<Type> out = new ArrayList<>();
			int offset = 1;
			int[] offsetOut = new int[1];
			while (chars[offset] != ')') {
				out.add(parseSingleType(chars, offset, offsetOut));
				offset = offsetOut[0];
			}
			out.add(parseSingleType(chars, offset + 1, offsetOut));
			return out.toArray(new Type[0]);
		}
		catch (RuntimeException ex) {
			if (!(ex instanceof IllegalArgumentException)) {
				ex = new IllegalArgumentException("Invalid descriptor '%s'".formatted(descriptor), ex);
			}
			throw ex;
		}
	}
	
	public static Type parseMethodReturnType(String descriptor) {
		try {
			char[] chars = descriptor.toCharArray();
			int offset = descriptor.lastIndexOf(')') + 1;
			return parseSingleType(chars, offset, new int[1]);
		}
		catch (RuntimeException ex) {
			if (!(ex instanceof IllegalArgumentException)) {
				ex = new IllegalArgumentException("Invalid descriptor '%s'".formatted(descriptor), ex);
			}
			throw ex;
		}
	}
	
	private static Type parseSingleType(char[] chars, int offset, int[] offsetOut) {
		try {
			char first = chars[offset];
			offset += 1;
			switch (first) {
			case '[':
				int dimensions = 1;
				while (chars[offset] == '[') {
					offset += 1;
					dimensions += 1;
				}
				
				Type inner = parseSingleType(chars, offset, offsetOut);
				offset = offsetOut[0];
				return new ArrayType(dimensions, inner);
			case 'L':
				int start = offset;
				while (chars[offset] != ';') {
					offset += 1;
				}
				
				int length = offset - start;
				offset += 1;
				return new ClassType(new String(chars, start, length));
			case 'V':
				return PrimitiveType.VOID;
			case 'Z':
			case 'B':
				return PrimitiveType.BYTE;
			case 'S':
				return PrimitiveType.SHORT;
			case 'I':
				return PrimitiveType.INT;
			case 'J':
				return PrimitiveType.LONG;
			case 'C':
				return PrimitiveType.CHAR;
			case 'F':
				return PrimitiveType.FLOAT;
			case 'D':
				return PrimitiveType.DOUBLE;
			}
			throw new IllegalArgumentException("Unknown descriptor identifier: %s (0x%s)".formatted(first, Integer.toHexString(first)));
		}
		finally {
			offsetOut[0] = offset;
		}
	}
}
