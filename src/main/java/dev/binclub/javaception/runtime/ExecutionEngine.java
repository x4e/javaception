package dev.binclub.javaception.runtime;

import dev.binclub.javaception.classfile.CodeAttribute;
import dev.binclub.javaception.classfile.MethodInfo;
import dev.binclub.javaception.classfile.OpcodeStride;
import dev.binclub.javaception.classfile.constants.ClassInfo;
import dev.binclub.javaception.classfile.constants.RefInfo;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;
import dev.binclub.javaception.type.PrimitiveType;
import dev.binclub.javaception.utils.ByteUtils;

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
		int index = 0;
		// reference to self
		if (!Modifier.isStatic(method.access)) {
			methodContext.store(0, instance);
			index += 1;
		}
		// store args into localvariables
		for (Object obj : args) {
			methodContext.store(index, obj);
			if (obj instanceof Long || obj instanceof Double) {
				index += 2;
			} else {
				++index;
			}
		}
		byte[] instructions = code.getCode();
		int currentInstruction = code.codeOffset;
		int opcode = ByteUtils.readUnsignedByte(instructions, currentInstruction);
		int branchOffset = 0;
		while (opcode < IRETURN || opcode > RETURN) {
			switch (opcode) {
			case NOP:
				break;
			case ACONST_NULL:
				methodContext.push(null);
				break;
			case ICONST_M1:
			case ICONST_0:
			case ICONST_1:
			case ICONST_2:
			case ICONST_3:
			case ICONST_4:
			case ICONST_5:
				methodContext.push(opcode - ICONST_0);
				break;
			case LCONST_0:
			case LCONST_1:
				methodContext.push((long) (opcode - LCONST_0));
				break;
			case FCONST_0:
			case FCONST_1:
			case FCONST_2:
				methodContext.push((float) (opcode - FCONST_0));
				break;
			case DCONST_0:
			case DCONST_1:
				methodContext.push((double) (opcode - DCONST_0));
				break;
			case BIPUSH:
				methodContext.push((int) (instructions[currentInstruction + 1]));
				break;
			case SIPUSH:
				methodContext.push((instructions[currentInstruction + 1] << 8) | instructions[currentInstruction + 2]);
			case LDC:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case LDC_W:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case LDC2_W:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case ILOAD:
			case LLOAD:
			case FLOAD:
			case DLOAD:
			case ALOAD:
				index = ByteUtils.readUnsignedByte(instructions, currentInstruction + 1);
				methodContext.push(methodContext.load(index));
				break;
			case ILOAD_0:
			case ILOAD_1:
			case ILOAD_2:
			case ILOAD_3:
			case LLOAD_0:
			case LLOAD_1:
			case LLOAD_2:
			case LLOAD_3:
			case FLOAD_0:
			case FLOAD_1:
			case FLOAD_2:
			case FLOAD_3:
			case DLOAD_0:
			case DLOAD_1:
			case DLOAD_2:
			case DLOAD_3:
			case ALOAD_0:
			case ALOAD_1:
			case ALOAD_2:
			case ALOAD_3:
				methodContext.push(methodContext.load((opcode + 2) % 4));
				break;
			case IALOAD:
			case LALOAD:
			case FALOAD:
			case DALOAD:
			case AALOAD:
			case BALOAD:
			case CALOAD:
			case SALOAD:
				index = (int) methodContext.pop();
				Object[] arr = (Object[]) methodContext.pop();
				methodContext.push(arr[index]);
				break;
			case ISTORE:
			case LSTORE:
			case FSTORE:
			case DSTORE:
			case ASTORE:
				index = ByteUtils.readUnsignedByte(instructions, currentInstruction + 1);
				methodContext.store(index, methodContext.pop());
				break;
			case ISTORE_0:
			case ISTORE_1:
			case ISTORE_2:
			case ISTORE_3:
			case LSTORE_0:
			case LSTORE_1:
			case LSTORE_2:
			case LSTORE_3:
			case FSTORE_0:
			case FSTORE_1:
			case FSTORE_2:
			case FSTORE_3:
			case DSTORE_0:
			case DSTORE_1:
			case DSTORE_2:
			case DSTORE_3:
			case ASTORE_0:
			case ASTORE_1:
			case ASTORE_2:
			case ASTORE_3:
				methodContext.store((opcode + 1) % 4, methodContext.pop());
				break;
			case IASTORE:
			case LASTORE:
			case FASTORE:
			case DASTORE:
			case AASTORE:
			case BASTORE:
			case CASTORE:
			case SASTORE:
				Object val = methodContext.pop();
				index = (int) methodContext.pop();
				arr = (Object[]) methodContext.pop();
				arr[index] = val;
				break;
			case POP:
				methodContext.pop();
				break;
			case POP2:
				methodContext.pop();
				methodContext.pop();
				break;
			case DUP:
				val = methodContext.pop();
				methodContext.push(val);
				methodContext.push(val);
				break;
			case DUP_X1:
				val = methodContext.pop();
				Object val2 = methodContext.pop();
				methodContext.push(val);
				methodContext.push(val2);
				methodContext.push(val);
				break;
			case DUP_X2:
				val = methodContext.pop();
				val2 = methodContext.pop();
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
				break;
			case DUP2:
				val = methodContext.pop();
				val2 = methodContext.pop();
				methodContext.push(val2);
				methodContext.push(val);
				methodContext.push(val2);
				methodContext.push(val);
				break;
			case DUP2_X1:
				val = methodContext.pop();
				val2 = methodContext.pop();
				val3 = methodContext.pop();
				methodContext.push(val2);
				methodContext.push(val);
				methodContext.push(val3);
				methodContext.push(val2);
				methodContext.push(val);
				break;
			case DUP2_X2:
				val = methodContext.pop();
				val2 = methodContext.pop();
				val3 = methodContext.pop();
				Object val4 = methodContext.pop();
				methodContext.push(val2);
				methodContext.push(val);
				methodContext.push(val4);
				methodContext.push(val3);
				methodContext.push(val2);
				methodContext.push(val);
				break;
			case SWAP:
				val = methodContext.pop();
				val2 = methodContext.pop();
				methodContext.push(val);
				methodContext.push(val2);
			case IADD:
				methodContext.push((int) methodContext.pop() + (int) methodContext.pop());
				break;
			case LADD:
				methodContext.push((long) methodContext.pop() + (long) methodContext.pop());
				break;
			case FADD:
				methodContext.push((float) methodContext.pop() + (float) methodContext.pop());
				break;
			case DADD:
				methodContext.push((double) methodContext.pop() + (double) methodContext.pop());
				break;
			case ISUB:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((int) val - (int) val2);
				break;
			case LSUB:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((long) val - (long) val2);
				break;
			case FSUB:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((float) val - (float) val2);
				break;
			case DSUB:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((double) val - (double) val2);
				break;
			case IMUL:
				methodContext.push((int) methodContext.pop() * (int) methodContext.pop());
				break;
			case LMUL:
				methodContext.push((long) methodContext.pop() * (long) methodContext.pop());
				break;
			case FMUL:
				methodContext.push((float) methodContext.pop() * (float) methodContext.pop());
				break;
			case DMUL:
				methodContext.push((double) methodContext.pop() * (double) methodContext.pop());
				break;
			case IDIV:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((int) val / (int) val2);
				break;
			case LDIV:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((long) val / (long) val2);
				break;
			case FDIV:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((float) val / (float) val2);
				break;
			case DDIV:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((double) val / (double) val2);
				break;
			case IREM:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((int) val % (int) val2);
				break;
			case LREM:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((long) val % (long) val2);
				break;
			case FREM:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((float) val % (float) val2);
				break;
			case DREM:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((double) val % (double) val2);
				break;
			case INEG:
				methodContext.push((~(int) methodContext.pop()) + 1);
			case FNEG:
				methodContext.push(-(float) methodContext.pop());
				break;
			case LNEG:
				methodContext.push((~(long) methodContext.pop()) + 1);
				break;
			case DNEG:
				methodContext.push(-(double) methodContext.pop());
				break;
			case ISHL:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((int) val << ((int) val2 & 0x1f));
			case LSHL:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((long) val << ((long) val2 & 0x3f));
				break;
			case ISHR:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((int) val >> ((int) val2 & 0x1f));
				break;
			case LSHR:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((long) val >> ((long) val2 & 0x3f));
				break;
			case IUSHR:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((int) val >>> ((int) val2 & 0x3f));
				break;
			case LUSHR:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((long) val >>> ((long) val2 & 0x3f));
				break;
			case IAND:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((int) val & (int) val2);
				break;
			case LAND:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((long) val & (long) val2);
				break;
			case IOR:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((int) val | (int) val2);
				break;
			case LOR:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((long) val | (long) val2);
				break;
			case IXOR:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((int) val ^ (int) val2);
				break;
			case LXOR:
				val2 = methodContext.pop();
				val = methodContext.pop();
				methodContext.push((long) val ^ (long) val2);
				break;
			case IINC:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case I2L:
				int i = (int) methodContext.pop();
				methodContext.push((long) i);
				break;
			case I2F:
				i = (int) methodContext.pop();
				methodContext.push((float) i);
				break;
			case I2D:
				i = (int) methodContext.pop();
				methodContext.push((double) i);
				break;
			case L2I:
				long l = (long) methodContext.pop();
				methodContext.push((int) l);
				break;
			case L2F:
				l = (long) methodContext.pop();
				methodContext.push((float) l);
				break;
			case L2D:
				l = (long) methodContext.pop();
				methodContext.push((double) l);
				break;
			case F2I:
				float f = (float) methodContext.pop();
				methodContext.push((int) f);
				break;
			case F2L:
				f = (float) methodContext.pop();
				methodContext.push((long) f);
				break;
			case F2D:
				f = (float) methodContext.pop();
				methodContext.push((double) f);
				break;
			case D2I:
				double d = (double) methodContext.pop();
				methodContext.push((int) d);
				break;
			case D2L:
				d = (double) methodContext.pop();
				methodContext.push((long) d);
				break;
			case D2F:
				d = (double) methodContext.pop();
				methodContext.push((float) d);
				break;
			case I2B:
				i = (int) methodContext.pop();
				methodContext.push((byte) i);
				break;
			case I2C:
				i = (int) methodContext.pop();
				methodContext.push((char) i);
				break;
			case I2S:
				i = (int) methodContext.pop();
				methodContext.push((short) i);
				break;
			case LCMP:
				if (methodContext.pop().equals(methodContext.pop())) {
					methodContext.push(0);
				} else {
					if ((long) methodContext.pop() < (long) methodContext.pop()) {
						methodContext.push(1);
					} else {
						methodContext.push(-1);
					}
					
				}
				break;
			case FCMPL:
			case FCMPG:
				f = (float) methodContext.pop();
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
				
				break;
			case DCMPL:
			case DCMPG:
				d = (double) methodContext.pop();
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
				break;
			case IFEQ:
				if ((int) methodContext.pop() == 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IFNE:
				if ((int) methodContext.pop() != 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IFLT:
				if ((int) methodContext.pop() < 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IFGE:
				if ((int) methodContext.pop() >= 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IFGT:
				if ((int) methodContext.pop() > 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IFLE:
				if ((int) methodContext.pop() <= 0) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IF_ICMPEQ:
				if ((int) methodContext.pop() == (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IF_ICMPNE:
				if ((int) methodContext.pop() != (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IF_ICMPLT:
				if ((int) methodContext.pop() > (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IF_ICMPGE:
				if ((int) methodContext.pop() <= (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IF_ICMPGT:
				if ((int) methodContext.pop() < (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IF_ICMPLE:
				if ((int) methodContext.pop() >= (int) methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IF_ACMPEQ:
				if (methodContext.pop() == methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IF_ACMPNE:
				if (methodContext.pop() != methodContext.pop()) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case GOTO:
				branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				break;
			case JSR:
				branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				methodContext.push(currentInstruction + 3);
				break;
			case JSR_W:
				branchOffset = ByteUtils.readInt(instructions, currentInstruction + 1);
				methodContext.push(currentInstruction + 5);
				break;
			case RET:
				index = ByteUtils.readUnsignedByte(instructions, currentInstruction + 1);
				currentInstruction = (int) methodContext.load(index);
				break;
			case TABLESWITCH:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case LOOKUPSWITCH:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case GETSTATIC:
				index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				RefInfo ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				methodContext.push(method.owner.staticFields[ref.getID(owner)]);
				break;
			case PUTSTATIC:
				index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				method.owner.staticFields[ref.getID(owner)] = methodContext.pop();
				break;
			case GETFIELD:
				index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				methodContext.push(((InstanceOop) methodContext.pop()).fields[ref.getID(owner)]);
				break;
			case PUTFIELD:
				index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				((InstanceOop) methodContext.pop()).fields[ref.getID(owner)] = methodContext.pop();
				break;
			case INVOKEVIRTUAL:
			case INVOKESPECIAL:
				index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
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
				break;
			case INVOKESTATIC:
				index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				ref = (RefInfo) method.owner.runtimeConstantPool[index - 1];
				Klass methodOwner = ref.getOwner(owner);
				targetMethod = methodOwner.methods[ref.getID(owner)];
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
				break;
			case INVOKEINTERFACE:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case INVOKEDYNAMIC:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case NEW:
				index = ByteUtils.readUnsignedShort(instructions, currentInstruction + 1);
				ClassInfo classInfo = (ClassInfo) method.owner.runtimeConstantPool[index - 1];
				methodContext.push(classInfo.getKlass(owner).newInstance());
				break;
			case NEWARRAY:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case ANEWARRAY:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case ATHROW:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case CHECKCAST:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case INSTANCEOF:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case MONITORENTER:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case MONITOREXIT:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case WIDE:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case MULTIANEWARRAY:
				throw new UnsupportedOperationException("Opcode not supported " + opcode);
			case IFNULL:
				if (methodContext.pop() == null) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case IFNONNULL:
				if (methodContext.pop() != null) {
					branchOffset = ByteUtils.readShort(instructions, currentInstruction + 1);
				}
				break;
			case GOTO_W:
				branchOffset = ByteUtils.readInt(instructions, currentInstruction + 1);
				break;
			default:
				System.out.println("Unsupported instruction");
				break;
			}
			if (opcode != RET) {
				if (branchOffset != 0) {
					System.out.println("branching");
					currentInstruction += branchOffset;
					branchOffset = 0;
				} else {
					int strideAmount = OpcodeStride.getStrideAmount(opcode, currentInstruction, instructions);
					currentInstruction += strideAmount + 1;
					opcode = ByteUtils.readUnsignedByte(instructions, currentInstruction);
				}
			}
			
		}
		//check to make sure we don't try to pop when the stack is expected to be empty
		if (opcode != RETURN) {
			return methodContext.pop();
		} else {
			return null;
		}
		
	}
}
