package dev.binclub.javaception.runtime;

import dev.binclub.javaception.classfile.CodeAttribute;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.classfile.OpcodeStride;
import dev.binclub.javaception.classfile.constants.ClassInfo;
import dev.binclub.javaception.classfile.constants.RefInfo;
import dev.binclub.javaception.classfile.constants.StringInfo;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.type.PrimitiveType;
import dev.binclub.javaception.utils.ByteUtils;
import profiler.Profiler;

import java.lang.reflect.Modifier;

import static dev.binclub.javaception.classfile.ClassFileConstants.*;

public class ExecutionEngine {
	// invokes method expecting a return obj to but put onto the caller stack
	public static Object invokeMethodObj(Klass owner, InstanceOop instance, MethodInfo method, Object... args) {
		if (!method.owner.resolved) {
			method.owner.resolve();
		}
		CodeAttribute code = method.code;
		MethodContext methodContext = new MethodContext(code.getMaxStack(), code.getMaxLocals());

		// Store arguments in local variables
		{
			int index = 0;
			// reference to self
			if (!Modifier.isStatic(method.access)) {
				methodContext.store(0, instance);
				index += 1;
			}
			for (Object obj : args) {
				methodContext.store(index, obj);
				if (obj instanceof Long || obj instanceof Double) {
					index += 2;
				} else {
					++index;
				}
			}
		}

		byte[] instructions = code.getCode();
		int currentInstruction = code.codeOffset;
		int opcode = ByteUtils.readUnsignedByte(instructions, currentInstruction);
		int branchOffset = 0;
		Profiler.start(method);
		while (opcode < IRETURN || opcode > RETURN) {
			switch (opcode) {
			case NOP -> {}
			case ACONST_NULL -> methodContext.push(null);
			case ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5 ->
				methodContext.push(opcode - ICONST_0);
			case LCONST_0, LCONST_1 ->
				methodContext.push((long) (opcode - LCONST_0));
			case FCONST_0, FCONST_1, FCONST_2 ->
				methodContext.push((float) (opcode - FCONST_0));
			case DCONST_0, DCONST_1 ->
				methodContext.push((double) (opcode - DCONST_0));
			case BIPUSH ->
				methodContext.push((int) ByteUtils.readByte(instructions, currentInstruction + 1));
			case SIPUSH ->
				methodContext.push((int) ByteUtils.readShort(instructions, currentInstruction + 1));
			case LDC -> {
				Object constant = method.owner.runtimeConstantPool[ByteUtils.readUnsignedByte(instructions, currentInstruction + 1) - 1];
				if(constant instanceof StringInfo){
					constant = ((StringInfo) constant).resolve(method.owner.runtimeConstantPool);
				}
				methodContext.push(constant);
			}
			case LDC_W, LDC2_W ->
				methodContext.push(method.owner.runtimeConstantPool[ByteUtils.readUnsignedShort(instructions, currentInstruction + 1) - 1]);
			case ILOAD, LLOAD, FLOAD, DLOAD, ALOAD -> {
				int index = ByteUtils.readUnsignedByte(instructions, currentInstruction + 1);
				methodContext.push(methodContext.load(index));
			}
			case ILOAD_0, ILOAD_1, ILOAD_2, ILOAD_3,
				LLOAD_0, LLOAD_1, LLOAD_2, LLOAD_3,
				FLOAD_0, FLOAD_1, FLOAD_2, FLOAD_3,
				DLOAD_0, DLOAD_1, DLOAD_2, DLOAD_3,
				ALOAD_0, ALOAD_1, ALOAD_2, ALOAD_3 ->
				methodContext.push(methodContext.load((opcode + 2) % 4));
			case IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD -> {
				int index = (int) methodContext.pop();
				Object[] arr = (Object[]) methodContext.pop();
				methodContext.push(arr[index]);
			}
			case ISTORE, LSTORE, FSTORE, DSTORE, ASTORE -> {
				int index = ByteUtils.readUnsignedByte(instructions, currentInstruction + 1);
				methodContext.store(index, methodContext.pop());
			}
			case ISTORE_0, ISTORE_1, ISTORE_2, ISTORE_3,
				LSTORE_0, LSTORE_1, LSTORE_2, LSTORE_3,
				FSTORE_0, FSTORE_1, FSTORE_2, FSTORE_3,
				DSTORE_0, DSTORE_1, DSTORE_2, DSTORE_3,
				ASTORE_0, ASTORE_1, ASTORE_2, ASTORE_3 ->
				methodContext.store((opcode + 1) % 4, methodContext.pop());
			case IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE -> {
				Object val = methodContext.pop();
				int index = (int) methodContext.pop();
				Object[] arr = (Object[]) methodContext.pop();
				arr[index] = val;
			}
			case POP ->
				methodContext.pop();
			case POP2 -> {
				methodContext.pop();
				methodContext.pop();
			}
			case DUP ->
				methodContext.push(methodContext.peek());
			case DUP_X1 -> {
				Object val = methodContext.pop();
				Object val2 = methodContext.pop();
				methodContext.push(val);
				methodContext.push(val2);
				methodContext.push(val);
			}
			case DUP_X2 -> {
				Object val = methodContext.pop();
				Object val2 = methodContext.pop();
				Object val3 = methodContext.pop();
				if (val2 instanceof Double || val2 instanceof Long) {
					methodContext.push(val);
					methodContext.push(val2);
					methodContext.push(val);
				} else {
					methodContext.push(val);
					methodContext.push(val3);
					methodContext.push(val2);
					methodContext.push(val);
				}
			}
			case DUP2 -> {
				Object val = methodContext.pop();
				Object val2 = methodContext.pop();
				methodContext.push(val2);
				methodContext.push(val);
				methodContext.push(val2);
				methodContext.push(val);
			}
			case DUP2_X1 -> {
				Object val = methodContext.pop();
				Object val2 = methodContext.pop();
				Object val3 = methodContext.pop();
				methodContext.push(val2);
				methodContext.push(val);
				methodContext.push(val3);
				methodContext.push(val2);
				methodContext.push(val);
			}
			case DUP2_X2 -> {
				Object val = methodContext.pop();
				Object val2 = methodContext.pop();
				Object val3 = methodContext.pop();
				Object val4 = methodContext.pop();
				methodContext.push(val2);
				methodContext.push(val);
				methodContext.push(val4);
				methodContext.push(val3);
				methodContext.push(val2);
				methodContext.push(val);
			}
			case SWAP -> {
				Object val = methodContext.pop();
				Object val2 = methodContext.pop();
				methodContext.push(val);
				methodContext.push(val2);
			}
			case IADD ->
				methodContext.push((int) methodContext.pop() + (int) methodContext.pop());
			case LADD ->
				methodContext.push((long) methodContext.pop() + (long) methodContext.pop());
			case FADD ->
				methodContext.push((float) methodContext.pop() + (float) methodContext.pop());
			case DADD ->
				methodContext.push((double) methodContext.pop() + (double) methodContext.pop());
			case ISUB -> {
				int val2 = (int) methodContext.pop();
				int val = (int) methodContext.pop();
				methodContext.push(val - val2);
			}
			case LSUB -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((long) val - (long) val2);
			}
			case FSUB -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((float) val - (float) val2);
			}
			case DSUB -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((double) val - (double) val2);
			}
			case IMUL ->
				methodContext.push((int) methodContext.pop() * (int) methodContext.pop());
			case LMUL ->
				methodContext.push((long) methodContext.pop() * (long) methodContext.pop());
			case FMUL ->
				methodContext.push((float) methodContext.pop() * (float) methodContext.pop());
			case DMUL ->
				methodContext.push((double) methodContext.pop() * (double) methodContext.pop());
			case IDIV -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((int) val / (int) val2);
			}
			case LDIV -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((long) val / (long) val2);
			}
			case FDIV -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((float) val / (float) val2);
			}
			case DDIV -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((double) val / (double) val2);
			}
			case IREM -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((int) val % (int) val2);
			}
			case LREM -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((long) val % (long) val2);
			}
			case FREM -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((float) val % (float) val2);
			}
			case DREM -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((double) val % (double) val2);
			}
			case INEG ->
				methodContext.push((~(int) methodContext.pop()) + 1);
			case FNEG ->
				methodContext.push(-(float) methodContext.pop());
			case LNEG ->
				methodContext.push((~(long) methodContext.pop()) + 1);
			case DNEG ->
				methodContext.push(-(double) methodContext.pop());
			case ISHL -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((int) val << ((int) val2 & 0x1f));
			}
			case LSHL -> {
				Object 	val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((long) val << ((long) val2 & 0x3f));
			}
			case ISHR -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((int) val >> ((int) val2 & 0x1f));
			}
			case LSHR -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((long) val >> ((long) val2 & 0x3f));
			}
			case IUSHR -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((int) val >>> ((int) val2 & 0x3f));
			}
			case LUSHR -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((long) val >>> ((long) val2 & 0x3f));
			}
			case IAND -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((int) val & (int) val2);
			}
			case LAND -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((long) val & (long) val2);
			}
			case IOR -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((int) val | (int) val2);
			}
			case LOR -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((long) val | (long) val2);
			}
			case IXOR -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((int) val ^ (int) val2);
			}
			case LXOR -> {
				Object val2 = methodContext.pop();
				Object val = methodContext.pop();
				methodContext.push((long) val ^ (long) val2);
			}
			case IINC -> {
				int index = ByteUtils.readUnsignedByte(instructions, currentInstruction + 1);
				int toAdd = ByteUtils.readByte(instructions, currentInstruction + 2);
				methodContext.store(index, (int) methodContext.load(index) + toAdd);
			}
			case I2L -> {
				int i = (int) methodContext.pop();
				methodContext.push((long) i);
			}
			case I2F -> {
				int i = (int) methodContext.pop();
				methodContext.push((float) i);
			}
			case I2D -> {
				int i = (int) methodContext.pop();
				methodContext.push((double) i);
			}
			case L2I -> {
				long l = (long) methodContext.pop();
				methodContext.push((int) l);
			}
			case L2F -> {
				long l = (long) methodContext.pop();
				methodContext.push((float) l);
			}
			case L2D -> {
				long l = (long) methodContext.pop();
				methodContext.push((double) l);
			}
			case F2I -> {
				float f = (float) methodContext.pop();
				methodContext.push((int) f);
			}
			case F2L -> {
				float f = (float) methodContext.pop();
				methodContext.push((long) f);
			}
			case F2D -> {
				float f = (float) methodContext.pop();
				methodContext.push((double) f);
			}
			case D2I -> {
				double d = (double) methodContext.pop();
				methodContext.push((int) d);
			}
			case D2L -> {
				double d = (double) methodContext.pop();
				methodContext.push((long) d);
			}
			case D2F -> {
				double d = (double) methodContext.pop();
				methodContext.push((float) d);
			}
			case I2B -> {
				int i = (int) methodContext.pop();
				methodContext.push((byte) i);
			}
			case I2C -> {
				int i = (int) methodContext.pop();
				methodContext.push((char) i);
			}
			case I2S -> {
				int i = (int) methodContext.pop();
				methodContext.push((short) i);
			}
			case LCMP -> {
				if (methodContext.pop().equals(methodContext.pop())) {
					methodContext.push(0);
				} else {
					if ((long) methodContext.pop() < (long) methodContext.pop()) {
						methodContext.push(1);
					} else {
						methodContext.push(-1);
					}
				}
			}
			case FCMPL, FCMPG -> {
				float f = (float) methodContext.pop();
				float f2 = (float) methodContext.pop();
				if (Float.isNaN(f) || Float.isNaN(f2)) {
					if (opcode == FCMPL) {
						methodContext.push(-1);
					} else {
						methodContext.push(1);
					}
				} else {
					if (methodContext.pop().equals(methodContext.pop())) {
						methodContext.push(0);
					} else {
						if ((float) methodContext.pop() < (float) methodContext.pop()) {
							methodContext.push(1);
						} else {
							methodContext.push(-1);
						}
					}
				}
			}
			case DCMPL, DCMPG -> {
				double d = (double) methodContext.pop();
				double d2 = (double) methodContext.pop();
				if (Double.isNaN(d) || Double.isNaN(d2)) {
					if (opcode == DCMPL) {
						methodContext.push(-1);
					} else {
						methodContext.push(1);
					}
				} else {
					if (methodContext.pop().equals(methodContext.pop())) {
						methodContext.push(0);
					} else {
						if ((double) methodContext.pop() < (double) methodContext.pop()) {
							methodContext.push(1);
						} else {
							methodContext.push(-1);
						}
					}
				}
			}
			case IFEQ -> {
				if ((int) methodContext.pop() == 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IFNE -> {
				if ((int) methodContext.pop() != 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IFLT -> {
				if ((int) methodContext.pop() < 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IFGE -> {
				if ((int) methodContext.pop() >= 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IFGT -> {
				if ((int) methodContext.pop() > 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IFLE -> {
				if ((int) methodContext.pop() <= 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IF_ICMPEQ -> {
				if ((int) methodContext.pop() == (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IF_ICMPNE -> {
				if ((int) methodContext.pop() != (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IF_ICMPLT -> {
				if ((int) methodContext.pop() > (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IF_ICMPGE -> {
				if ((int) methodContext.pop() <= (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IF_ICMPGT -> {
				if ((int) methodContext.pop() < (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IF_ICMPLE -> {
				if ((int) methodContext.pop() >= (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IF_ACMPEQ -> {
				if (methodContext.pop() == methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IF_ACMPNE -> {
				if (methodContext.pop() != methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case GOTO ->
				branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
			case JSR -> {
				branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				methodContext.push(currentInstruction + 3);
			}
			case JSR_W -> {
				branchOffset = ByteUtils.readInt(instructions, currentInstruction + 1);
				methodContext.push(currentInstruction + 5);
			}
			case RET -> {
				int index = ByteUtils.readUnsignedByte(instructions, currentInstruction + 1);
				currentInstruction = (int) methodContext.load(index);
			}
			case TABLESWITCH ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case LOOKUPSWITCH ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case GETSTATIC -> {
				int index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				RefInfo ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				methodContext.push(method.owner.staticFields[ref.getID(owner)]);
			}
			case PUTSTATIC -> {
				int index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				RefInfo ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				method.owner.staticFields[ref.getID(owner)] = methodContext.pop();
			}
			case GETFIELD -> {
				int index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				RefInfo ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				methodContext.push(((InstanceOop) methodContext.pop()).fields[ref.getID(owner)]);
			}
			case PUTFIELD -> {
				int index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				RefInfo ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				Object toPut = methodContext.pop();
				((InstanceOop) methodContext.pop()).fields[ref.getID(owner)] = toPut;
			}
			case INVOKEVIRTUAL, INVOKESPECIAL -> {
				int index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				RefInfo ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				MethodInfo targetMethod = ref.getOwner(owner).methods[ref.getID(owner)];
				InstanceOop inst = (InstanceOop) methodContext.pop();
				Object ret;
				if (targetMethod.descriptor.length == 1) {
					ret = ExecutionEngine.invokeMethodObj(inst.getKlass(), inst, targetMethod);
				} else {
					Object[] params = new Object[targetMethod.descriptor.length - 1];
					for (int param = targetMethod.descriptor.length - 1; param > -1; param--) {
						params[param] = methodContext.pop();
					}
					ret = ExecutionEngine.invokeMethodObj(inst.getKlass(), inst, targetMethod, params);
				}
				if (targetMethod.descriptor[targetMethod.descriptor.length - 1] != PrimitiveType.VOID) {
					methodContext.push(ret);
				}
			}
			case INVOKESTATIC -> {
				int index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				RefInfo ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				Klass methodOwner = ref.getOwner(owner);
				MethodInfo targetMethod = methodOwner.methods[ref.getID(owner)];
				Object ret;
				if (targetMethod.descriptor.length == 1) {
					ret = ExecutionEngine.invokeMethodObj(methodOwner, InstanceOop._null(), targetMethod);
				} else {
					Object[] params = new Object[targetMethod.descriptor.length];
					for (int param = targetMethod.descriptor.length - 1; param > -1; param--) {
						params[param] = methodContext.pop();
					}
					ret = ExecutionEngine.invokeMethodObj(methodOwner, InstanceOop._null(), targetMethod, params);
				}
				if (targetMethod.descriptor[targetMethod.descriptor.length - 1] != PrimitiveType.VOID) {
					methodContext.push(ret);
				}
			}
			case INVOKEINTERFACE ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case INVOKEDYNAMIC ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case NEW -> {
				int index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				ClassInfo classInfo = (ClassInfo) method.owner.runtimeConstantPool[index - 1];
				methodContext.push(classInfo.getKlass(owner).newInstance());
			}
			case NEWARRAY ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case ANEWARRAY -> {
				int size = (int) methodContext.pop();
				if (size < 0) {
					throw new NegativeArraySizeException();
				}
				methodContext.push(new Object[size]);
			}
			case ATHROW ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case CHECKCAST ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case INSTANCEOF ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case MONITORENTER ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case MONITOREXIT ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case WIDE ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case MULTIANEWARRAY ->
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case IFNULL -> {
				if (methodContext.pop() == null) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case IFNONNULL -> {
				if (methodContext.pop() != null) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
			}
			case GOTO_W -> {
				branchOffset = ByteUtils.readInt(instructions, currentInstruction + 1);
			}
			default ->
				throw new UnsupportedOperationException("Unsupported opcode " + opcode);
			}

			if (opcode != RET) {
				if (branchOffset != 0) {
					currentInstruction += branchOffset;
					branchOffset = 0;
					opcode = instructions[currentInstruction];
				} else {
					int strideAmount = OpcodeStride.getStrideAmount(opcode, currentInstruction, instructions);
					currentInstruction += strideAmount + 1;
					opcode = ByteUtils.readUnsignedByte(instructions, currentInstruction);
				}
			}
		}
		Profiler.finish(method);

		//check to make sure we don't try to pop when the stack is expected to be empty
		if (opcode != RETURN) {
			return methodContext.pop();
		} else {
			return null;
		}
	}
	
	public static void printAllProfileData() {
		Profiler.dataMap.forEach((name, pdata) -> {
			System.out.printf("%s took %d microseconds %n", name, pdata.getAverage() / 1000);
		});
	}
}
