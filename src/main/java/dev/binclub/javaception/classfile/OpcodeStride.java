package dev.binclub.javaception.classfile;

import java.util.Arrays;

public class OpcodeStride {
	
	static boolean initialized = false;
	// number of bytes this opcode takes e.g bipush 1byte
	// index correlates to bytecode
	static int[] strideAmount = new int[256];
	
	static void init() {
		// this method only needs to be ran once
		if (initialized) {
			return;
		}
		Arrays.fill(strideAmount, 0);
		strideAmount[0x10] = 1;
		strideAmount[0x11] = 2;
		strideAmount[0x12] = 1;
		strideAmount[0x13] = 2;
		strideAmount[0x14] = 2;
		setRange(0x15, 0x19, 1);
		setRange(0x36, 0x3a, 1);
		strideAmount[0x84] = 2;// inc
		setRange(0x99, 0xa8, 2);
		strideAmount[0xa9] = 1;// ret
		strideAmount[0xaa] = -1;
		strideAmount[0xab] = -1;
		setRange(0xb2, 0xb8, 2);
		strideAmount[0xb9] = 4;
		strideAmount[0xba] = 4;
		strideAmount[0xbb] = 2;
		strideAmount[0xbc] = 1;
		strideAmount[0xbd] = 2;
		setRange(0xc0, 0xc1, 2);
		strideAmount[0xc4] = -1;
		strideAmount[0xc5] = 3;
		setRange(0xc6, 0xc7, 2);
		setRange(0xc8, 0xc9, 4);
		initialized = true;
	}
	
	public static int getStrideAmount(int opcode, int byteCount, int[] instructions) {
		init();
		int stride = strideAmount[opcode];
		
		if (stride != -1) {
			return stride;
		}
		// wide
		if (opcode == 0xc4) {
			int opcodeW = instructions[byteCount + 1];
			// inc
			if (opcodeW == 0x84) {
				return 4;
			}
			else {
				return 2;
			}
		}
		// tableswitch
		if (opcode == 0xaa) {
			int padAmount = (byteCount + 1) % 4;
			if (padAmount != 0) {
				padAmount = 4 - padAmount;
			}
			stride += padAmount;
			stride += 12;
			// default
			int low = getInt(byteCount + padAmount + 4, instructions);
			int high = getInt(byteCount + padAmount + 8, instructions);
			int jumpOffsetCount = high - low + 1;
			stride += jumpOffsetCount * 4;
		}
		// lookupswitch
		if (opcode == 0xab) {
			int padAmount = (byteCount + 1) % 4;
			if (padAmount != 0) {
				padAmount = 4 - padAmount;
			}
			stride += padAmount;
			int npairsCount = getInt(byteCount + padAmount + 4, instructions);
			stride += 8;
			stride += npairsCount * 8;
		}
		return stride;
	}
	
	public static int getInt(int index, int[] instructions) {
		int b1 = instructions[index + 1];
		int b2 = instructions[index + 2];
		int b3 = instructions[index + 3];
		int b4 = instructions[index + 4];
		return (b1 << 24) + (b2 << 16) + (b3 << 8) + (b4);
	}
	
	static void setRange(int lowerBound, int upperBound, int count) {
		for (int i = lowerBound; i <= upperBound; i++) {
			strideAmount[i] = count;
		}
	}
}
