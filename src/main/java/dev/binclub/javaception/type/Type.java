package dev.binclub.javaception.type;

public abstract class Type {
	/**
	 * Parse a field descriptor according to JVMS 4.3.2.
	 * This does not support parameters or void.
	 * @param descriptor a field descriptor
	 * @return the singular type represented by the descriptor
	 * @throws IllegalArgumentException if the descriptor is invalid
	 */
	public static Type parseFieldDescriptor(String descriptor) {
		char[] chars = descriptor.toCharArray();
		try {
			return parseFieldDescriptor(chars, 0);
		} catch (RuntimeException ex) {
			if (!(ex instanceof IllegalArgumentException)) {
				ex = new IllegalArgumentException("Descriptor '" + descriptor + "'", ex);
			}
			throw ex;
		}
	}
	
	private static Type parseFieldDescriptor(char[] chars, int offset) {
		char first = chars[offset];
		// order is to ensure tableswitch is used
		switch (first) {
		case '[':
			int dimensions = 0;
			do {
				offset += 1;
				dimensions += 1;
			} while (chars[offset] == '[');
			
			Type inner = parseFieldDescriptor(chars, offset);
			return new ArrayType(dimensions, inner);
		case 'L':
			offset += 1;
			int start = offset;
			int end = start;
			while (chars[end] != ';') {
				end += 1;
			}
			
			int length = end - start;
			return new ClassType(new String(chars, start, length));
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
		throw new IllegalArgumentException("Unknown descriptor identifier: " + first);
	}
}
