package dev.binclub.javaception.classfile.constants;

import java.io.UTFDataFormatException;

import static dev.binclub.javaception.utils.ByteUtils.readUtf;

public class UtfInfo {
	private byte[] data;
	private int offset;
	private String inner;
	
	public UtfInfo(byte[] data, int offset) {
		this.data = data;
		this.offset = offset;
	}
	
	public String get() {
		String out = inner;
		if (out == null) {
			try {
				inner = readUtf(data, offset);
				data = null;
			} catch (UTFDataFormatException e) {
				var err = new ClassFormatError(e.getMessage());
				err.addSuppressed(e);
				throw err;
			}
		}
		return inner;
	}
}
