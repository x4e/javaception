package dev.binclub.javaception.classfile.constants;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UtfInfo {
	private final byte[] data;
	private String inner;
	
	public UtfInfo(DataInputStream ds) throws IOException {
		int length = ds.readUnsignedShort();
		data = new byte[length];
		ds.readFully(data);
	}
	
	public String get() {
		String out = inner;
		if (out == null) inner = new String(data, StandardCharsets.UTF_8);
		return inner;
	}
}
