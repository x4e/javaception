package dev.binclub.javaception.classfile;

import static dev.binclub.javaception.classfile.ClassFileConstants.*;

public class OpcodeStride {
	// number of bytes an opcodes operands take
	// -1 for variable length operands
	static int[] strideAmount = new int[OPC_MAX];
	
	static {
		strideAmount[BIPUSH] = 1;
		strideAmount[SIPUSH] = 2;
		strideAmount[LDC] = 1;
		strideAmount[LDC_W] = 2;
		strideAmount[LDC2_W] = 2;
		setRange(ILOAD, ALOAD, 1);
		setRange(ISTORE, ASTORE, 1);
		strideAmount[IINC] = 2;
		setRange(IFEQ, JSR, 2);
		strideAmount[RET] = 1;
		strideAmount[TABLESWITCH] = -1;
		strideAmount[LOOKUPSWITCH] = -1;
		setRange(GETSTATIC, INVOKESTATIC, 2);
		strideAmount[INVOKEINTERFACE] = 4;
		strideAmount[INVOKEDYNAMIC] = 4;
		strideAmount[NEW] = 2;
		strideAmount[NEWARRAY] = 1;
		strideAmount[ANEWARRAY] = 2;
		setRange(CHECKCAST, INSTANCEOF, 2);
		strideAmount[WIDE] = -1;
		strideAmount[MULTIANEWARRAY] = 3;
		setRange(IFNULL, IFNONNULL, 2);
		setRange(GOTO_W, JSR_W, 4);
	}
	
	public static int getStrideAmount(int opcode, int offset, int[] instructions) {
		int stride = strideAmount[opcode];
		
		if (stride != -1) {
			return stride;
		}
		if (opcode == WIDE) {
			int opcodeW = instructions[offset + 1];
			if (opcodeW == IINC) {
				return 4;
			} else {
				return 2;
			}
		}
		if (opcode == TABLESWITCH) {
			int padAmount = (offset + 1) % 4;
			if (padAmount != 0) {
				padAmount = 4 - padAmount;
			}
			stride += padAmount;
			stride += 12;
			// default
			int low = getInt(offset + padAmount + 4, instructions);
			int high = getInt(offset + padAmount + 8, instructions);
			int jumpOffsetCount = high - low + 1;
			stride += jumpOffsetCount * 4;
		}
		if (opcode == LOOKUPSWITCH) {
			int padAmount = (offset + 1) % 4;
			if (padAmount != 0) {
				padAmount = 4 - padAmount;
			}
			stride += padAmount;
			int npairsCount = getInt(offset + padAmount + 4, instructions);
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
