package dev.binclub.javaception.utils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;

public class ByteUtils {
	/**
	 * Reads the next byte of data from the input stream.
	 * The value byte is returned as an int in the range 0 to 255.
	 */
	public static int readUnsignedByte(byte[] data, int offset) {
		return data[offset] & 0xff;
	}
	
	public static boolean readBoolean(byte[] data, int offset) {
		int ch = readUnsignedByte(data, offset);
		return (ch != 0);
	}
	
	public static byte readByte(byte[] data, int offset) {
		return (byte) readUnsignedByte(data, offset);
	}
	
	public static short readShort(byte[] data, int offset) {
		int ch1 = readUnsignedByte(data, offset);
		int ch2 = readUnsignedByte(data, offset + 1);
		return (short)((ch1 << 8) + (ch2 << 0));
	}
	
	public static int readUnsignedShort(byte[] data, int offset) {
		int ch1 = readUnsignedByte(data, offset);
		int ch2 = readUnsignedByte(data, offset + 1);
		return (ch1 << 8) + (ch2 << 0);
	}
	
	public static char readChar(byte[] data, int offset) {
		int ch1 = readUnsignedByte(data, offset);
		int ch2 = readUnsignedByte(data, offset + 1);
		return (char)((ch1 << 8) + (ch2 << 0));
	}
	
	public static int readInt(byte[] data, int offset) {
		int ch1 = readUnsignedByte(data, offset);
		int ch2 = readUnsignedByte(data, offset + 1);
		int ch3 = readUnsignedByte(data, offset + 2);
		int ch4 = readUnsignedByte(data, offset + 3);
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}
	
	public static long readLong(byte[] data, int offset) {
		int ch1 = readUnsignedByte(data, offset);
		int ch2 = readUnsignedByte(data, offset + 1);
		int ch3 = readUnsignedByte(data, offset + 2);
		int ch4 = readUnsignedByte(data, offset + 3);
		int ch5 = readUnsignedByte(data, offset + 4);
		int ch6 = readUnsignedByte(data, offset + 5);
		int ch7 = readUnsignedByte(data, offset + 6);
		int ch8 = readUnsignedByte(data, offset + 7);
		return (((long)ch1 << 56) +
			((long)(ch2 & 255) << 48) +
			((long)(ch3 & 255) << 40) +
			((long)(ch4 & 255) << 32) +
			((long)(ch5 & 255) << 24) +
			((ch6 & 255) << 16) +
			((ch7 & 255) <<  8) +
			((ch8 & 255) <<  0));
	}
	
	public static float readFloat(byte[] data, int offset) {
		return Float.intBitsToFloat(readInt(data, offset));
	}
	
	public static double readDouble(byte[] data, int offset) {
		return Double.longBitsToDouble(readLong(data, offset));
	}
	
	public static String readUtf(byte[] data, int offset) throws UTFDataFormatException {
		int utflen = readUnsignedShort(data, offset);
		offset += 2;
		char[] chararr = new char[utflen];
		
		int c, char2, char3;
		int count = 0;
		int chararr_count=0;
		
		while (count < utflen) {
			c = (int) data[offset + count] & 0xff;
			if (c > 127) break;
			count++;
			chararr[chararr_count++]=(char)c;
		}
		
		while (count < utflen) {
			c = (int) data[offset + count] & 0xff;
			switch (c >> 4) {
			/* 0xxxxxxx*/
			case 0, 1, 2, 3, 4, 5, 6, 7 -> {
				count++;
				chararr[chararr_count++] = (char) c;
			}
			/* 110x xxxx   10xx xxxx*/
			case 12, 13 -> {
				count += 2;
				if (count > utflen)
					throw new UTFDataFormatException(
						"malformed input: partial character at end");
				char2 = data[offset + count - 1];
				if ((char2 & 0xC0) != 0x80)
					throw new UTFDataFormatException(
						"malformed input around byte " + count);
				chararr[chararr_count++] = (char) (((c & 0x1F) << 6) |
					(char2 & 0x3F));
			}
			/* 1110 xxxx  10xx xxxx  10xx xxxx */
			case 14 -> {
				count += 3;
				if (count > utflen)
					throw new UTFDataFormatException(
						"malformed input: partial character at end");
				char2 = data[offset + count - 2];
				char3 = data[offset + count - 1];
				if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
					throw new UTFDataFormatException(
						"malformed input around byte " + (count - 1));
				chararr[chararr_count++] = (char) (((c & 0x0F) << 12) |
					((char2 & 0x3F) << 6) |
					((char3 & 0x3F) << 0));
			}
			/* 10xx xxxx,  1111 xxxx */
			default -> throw new UTFDataFormatException(
				"malformed input around byte " + count);
			}
		}
		// The number of chars produced may be less than utflen
		return new String(chararr, 0, chararr_count);
	}
}
