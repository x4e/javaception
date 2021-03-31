package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classfile.attributes.LookupSwitchInstruction;
import dev.binclub.javaception.classfile.constants.ClassInfo;
import dev.binclub.javaception.classfile.constants.InvokeDynamicInfo;
import dev.binclub.javaception.classfile.constants.RefInfo;
import dev.binclub.javaception.classfile.instructions.*;

import java.util.ArrayList;
import java.util.List;

public class InstructionParser {
	
	public static List<SimpleInstruction> parseCode(int[] code, Object[] constantPool) {
		
		SimpleInstruction[] insts = new SimpleInstruction[code.length];
		int instructionPointer = 0;
		for (int i = 0; i < code.length; i++) {
			if (i == instructionPointer) {
				SimpleInstruction inst = InstructionParser.parseInstruction(code, i, constantPool);
				insts[i] = inst;
				instructionPointer += OpcodeStride.getStrideAmount(code[i], i, code) + 1;
			}
		}
		List<SimpleInstruction> instructions = new ArrayList<>();
		for (int i = 0; i < insts.length; i++) {
			SimpleInstruction inst = insts[i];
			if (inst instanceof BranchInstruction) {
				BranchInstruction instruct = (BranchInstruction) inst;
				instruct.setBranchInstruction(insts[instruct.getBranchPosition()]);
				if (instruct.getBranchInstruction() == null) {
					throw new VerifyError("Invalid branch instruction");
				}
			}
			if (inst instanceof TableSwitchInstruction) {
				TableSwitchInstruction instruct = (TableSwitchInstruction) inst;
				int padAmount = (i + 1) % 4;
				if (padAmount != 0) {
					padAmount = 4 - padAmount;
				}
				instruct.setDefaultInstruction(insts[instruct.defalt + i]);
				if (instruct.getDefaultInstruction() == null) {
					throw new VerifyError();
				}
				int count = instruct.high - instruct.low + 1;
				SimpleInstruction[] jumps = new SimpleInstruction[count];
				for (int j = 0; j < count; j++) {
					int offset = i + padAmount + 12 + (j * 4);
					jumps[j] = insts[i + OpcodeStride.getInt(offset, code)];
				}
				for (SimpleInstruction jump : jumps) {
					if (jump == null) {
						throw new VerifyError("Table switch invalid branch");
					}
				}
				instruct.setJumpOffsets(jumps);
			}
			if (inst instanceof LookupSwitchInstruction) {
				LookupSwitchInstruction instruct = (LookupSwitchInstruction) inst;
				int padAmount = (i + 1) % 4;
				if (padAmount != 0) {
					padAmount = 4 - padAmount;
				}
				instruct.setDefaultInstruction(insts[instruct.defalt + i]);
				if (instruct.getDefaultInstruction() == null) {
					throw new VerifyError();
				}
				SimpleInstruction[] jumps = new SimpleInstruction[instruct.npairs];
				int[] keys = new int[instruct.npairs];
				for (int j = 0; j < instruct.npairs; j++) {
					int offset = i + padAmount + 8 + (j * 8);
					keys[j] = OpcodeStride.getInt(offset, code);
					jumps[j] = insts[i + OpcodeStride.getInt(offset + 4, code)];
				}
				for (SimpleInstruction jump : jumps) {
					if (jump == null) {
						throw new VerifyError("LookupSwitchInstruction invalid branch");
					}
				}
				instruct.setJumpOffsets(jumps);
				instruct.setKeys(keys);
				
			}
			// insts is gapped
			if (inst != null) {
				instructions.add(inst);
			}
		}
		for (int i = 0; i < instructions.size(); i++) {
			SimpleInstruction instruction = instructions.get(i);
			int opcode = instruction.getOpcode();
			// return instructions should not have a next instruction anything after a
			// return is a branch
			if (opcode < 0xac || opcode > 0xb1 && i + 1 < instructions.size()) {
				instruction.setNextInstruction(instructions.get(i + 1));
			}
		}
		return instructions;
	}
	
	public static SimpleInstruction parseInstruction(int[] instructions, int index, Object[] constantPool) {
		int opcode = instructions[index];
		int stride = OpcodeStride.getStrideAmount(opcode, index, instructions);
		if (stride == 0) {
			return new SimpleInstruction(opcode);
		}
		// bipush / ldc // xload // xstore // ret // atype
		if (opcode == 0x10 || opcode == 0x12 || (opcode > 0x14 && opcode < 0x1a) || (opcode > 0x35 && opcode < 0x3b)
			|| opcode == 0xa8 || opcode == 0xbc) {
			int value = instructions[index + 1];
			return new VarInstruction(opcode, value);
		}
		// sipush
		if (opcode == 0x11) {
			int value = ((instructions[index + 1] << 8) + instructions[index + 2]);
			return new VarInstruction(opcode, value);
		}
		// if branch // ifnull // ifnonnull
		if ((opcode > 0x98 && opcode < 0xa9) || opcode == 0xc6 || opcode == 0xc7) {
			int branchOffset = (short) ((instructions[index + 1] << 8) + (instructions[index + 2] << 0));
			int pos = index + branchOffset;
			if (pos < 0 || pos > instructions.length) {
				throw new VerifyError();
			}
			return new BranchInstruction(opcode, pos);
		}
		// goto_w // jsr_W
		if (opcode == 0xc8 || opcode == 0xc9) {
			int branchOffset = ((instructions[index + 1] << 24) + (instructions[index + 2] << 16)
				+ (instructions[index + 3] << 8) + instructions[index + 4]);
			int pos = index + branchOffset;
			if (pos < 0 || pos > instructions.length) {
				throw new VerifyError();
			}
			return new BranchInstruction(opcode, pos);
		}
		// ldc
		if (opcode == 0x12) {
			Object constant = constantPool[instructions[index + 1] - 1];
			return new LoadConstantInstruction(opcode, constant);
		}
		// ldc_w || ldc2_w
		if (opcode == 0x13 || opcode == 0x14) {
			Object constant = constantPool[((instructions[index + 1] << 8) + instructions[index + 2]) - 1];
			return new LoadConstantInstruction(opcode, constant);
		}
		// getstatic -> invokedynamic
		if (opcode > 0xb1 && opcode < 0xba) {
			int value = ((instructions[index + 1] << 8) + instructions[index + 2]);
			return new RefInstruction(opcode, (RefInfo) constantPool[value - 1]);
		}
		if (opcode == 0xba) {
			int value = ((instructions[index + 1] << 8) + instructions[index + 2]);
			return new InvokeDynamicInstruction((InvokeDynamicInfo) constantPool[value - 1]);
		}
		// new // anewarray // checkcast // instanceof
		if (opcode == 0xc0 || opcode == 0xc1 || opcode == 0xbb || opcode == 0xbd) {
			int value = ((instructions[index + 1] << 8) + instructions[index + 2]);
			return new ClassRefInstruction(opcode, (ClassInfo) constantPool[value - 1]);
		}
		// multianewarray
		if (opcode == 0xc5) {
			int value = ((instructions[index + 1] << 8) + instructions[index + 2]);
			int dimensions = instructions[index + 3];
			return new MultiANewArray((ClassInfo) constantPool[value - 1], dimensions);
		}
		// inc
		if (opcode == 0x84) {
			int value = ((instructions[index + 1] << 8) + instructions[index + 2]);
			int increment = instructions[index + 3];
			return new IincInstruction(value, increment);
		}
		// wide
		if (opcode == 0xc4) {
			int op = instructions[index + 1];
			if (op == 0x84) {
				int value = ((instructions[index + 1] << 8) + instructions[index + 2]);
				int increment = (short) ((instructions[index + 3] << 8) + instructions[index + 4]);
				return new IincInstruction(value, increment);
			}
			else {
				int value = ((instructions[index + 1] << 8) + instructions[index + 2]);
				return new VarInstruction(op, value);
			}
		}
		// table switch
		if (opcode == 0xaa) {
			
			int padAmount = (index + 1) % 4;
			if (padAmount != 0) {
				padAmount = 4 - padAmount;
			}
			// misspelled on purpose
			int defalt = OpcodeStride.getInt(index + padAmount, instructions);
			int low = OpcodeStride.getInt(index + padAmount + 4, instructions);
			int high = OpcodeStride.getInt(index + padAmount + 8, instructions);
			return new TableSwitchInstruction(high, low, defalt);
		}
		// lookupswitch
		if (opcode == 0xab) {
			int padAmount = (index + 1) % 4;
			if (padAmount != 0) {
				padAmount = 4 - padAmount;
			}
			// misspelled on purpose
			int defalt = OpcodeStride.getInt(index + padAmount, instructions);
			int npairs = OpcodeStride.getInt(index + padAmount + 4, instructions);
			return new LookupSwitchInstruction(defalt, npairs);
		}
		
		throw new VerifyError("could not parse " + String.format("0x%2X", opcode));
	}
	
}
