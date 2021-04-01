package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classfile.attributes.*;
import dev.binclub.javaception.classfile.constants.*;
import dev.binclub.javaception.classloader.KlassLoader;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dev.binclub.javaception.classfile.ClassFileConstants.*;
import static dev.binclub.javaception.utils.ByteUtils.*;
import static dev.binclub.javaception.utils.ByteUtils.readUnsignedShort;

public class ClassFileParser {
	private static final int FIRST_SUPPORTED_VERSION = V1_2;
	private static final int LAST_SUPPORTED_VERSION = V16;
	
	private final byte[] data;
	private final int startOffset;
	private final int accessOffset;
	private int fieldsOffset;
	private int methodOffset;
	private int attributesOffset;
	
	private int _attrib_out_offset;
	
	public final Object[] constantPool;
	public final int constantPoolCount;
	public final int majorVersion;
	public final int minorVersion;
	public final int access;
	public final String className;
	public final Klass superClass;
	public final Klass[] interfaces;
	
	public final FieldInfo[] fields;
	public final MethodInfo[] methods;
	public final List<AttributeInfo> attributes;
	
	public ClassFileParser(byte[] data, InstanceOop loader) throws Throwable {
		this(data, 0, loader);
	}
	
	public ClassFileParser(byte[] data, int startOffset, InstanceOop loader) throws Throwable {
		this.data = data;
		this.startOffset = startOffset;
		
		if (readInt(data, startOffset) != CLASS_MAGIC) {
			throw new ClassFormatError();
		}
		
		minorVersion = readUnsignedShort(data, startOffset + 4);
		majorVersion = readUnsignedShort(data, startOffset + 6);
		if (majorVersion > LAST_SUPPORTED_VERSION || majorVersion < FIRST_SUPPORTED_VERSION) {
			throw new UnsupportedClassVersionError("Unsupported class file version: " + majorVersion + "." + minorVersion);
		}
		
		constantPoolCount = readUnsignedShort(data, startOffset + 8);
		constantPool = new Object[constantPoolCount - 1];
		accessOffset = parseConstantPool(startOffset + 10);
		
		access = readUnsignedShort(data, accessOffset);
		
		int classNameIndex = readUnsignedShort(data, accessOffset + 2);
		className = ((ClassInfo) constantPool[classNameIndex - 1]).name;
		int superClassNameIndex = readUnsignedShort(data, accessOffset + 4);
		superClass = KlassLoader.loadClass(loader, ((ClassInfo) constantPool[superClassNameIndex - 1]).name);
		
		int interfacesCount = readUnsignedShort(data, accessOffset + 6);
		interfaces = new Klass[interfacesCount];
		fieldsOffset = accessOffset + 8;
		for (int i = 0; i < interfacesCount; i++) {
			interfaces[i] = KlassLoader.loadClass(loader, (String) constantPool[readUnsignedShort(data, fieldsOffset) - 1]);
			fieldsOffset += 2;
		}
		
		if ((access & ACC_MODULE) != 0) {
			throw new NoClassDefFoundError("%s is not a class because access_flag ACC_MODULE is set".formatted(className));
		}
		
		int fieldsCount = readUnsignedShort(data, fieldsOffset);
		fields = new FieldInfo[fieldsCount];
		methodOffset = fieldsOffset + 2;
		for (int i = 0; i < fieldsCount; i++) {
			int access = readUnsignedShort(data, methodOffset);
			int nameIndex = readUnsignedShort(data, methodOffset + 2);
			String name = ((UtfInfo) constantPool[nameIndex - 1]).get();
			int descriptorIndex = readUnsignedShort(data, methodOffset + 4);
			String descriptor = ((UtfInfo) constantPool[descriptorIndex - 1]).get();
			List<AttributeInfo> attributes = readAttributes(methodOffset + 6, constantPool, 1);
			methodOffset = _attrib_out_offset;
			fields[i] = new FieldInfo(access, name, descriptor, attributes);
		}
		
		int methodsCount = readUnsignedShort(data, methodOffset);
		methods = new MethodInfo[methodsCount];
		attributesOffset = methodOffset + 2;
		for (int i = 0; i < methodsCount; i++) {
			int access = readUnsignedShort(data, attributesOffset);
			int nameIndex = readUnsignedShort(data, attributesOffset + 2);
			String name = ((UtfInfo) constantPool[nameIndex - 1]).get();
			int descriptorIndex = readUnsignedShort(data, attributesOffset + 4);
			String descriptor = ((UtfInfo) constantPool[descriptorIndex - 1]).get();
			List<AttributeInfo> attributes = readAttributes(attributesOffset + 6, constantPool, 2);
			attributesOffset = _attrib_out_offset;
			methods[i] = new MethodInfo(access, name, descriptor, attributes);
		}
		
		attributes = readAttributes(attributesOffset, constantPool, 0);
	}
	
	/**
	 * @param source 0 = class, 1 = field, 2 = method, 3 = code
	 */
	public List<AttributeInfo> readAttributes(int offset, Object[] constantPool, int source) throws IOException {
		int attributesCount = readUnsignedShort(data, offset);
		offset += 2;
		ArrayList<AttributeInfo> attributes = new ArrayList<>(attributesCount);
		for (int _attribIndex = 0; _attribIndex < attributesCount; _attribIndex++) {
			int attributeNameIndex = readUnsignedShort(data, offset);
			int attributeLength = readInt(data, offset + 2);
			offset += 6;
			int attributeStart = offset;
			String name = ((UtfInfo) constantPool[attributeNameIndex - 1]).get();
			switch (name) {
			case Attribute_Code -> {
				if (source == 2) {
					attributes.add(new CodeAttribute(this, data, offset, constantPool));
				}
				offset = attributeStart + attributeLength;
				continue;
			}
			case Attribute_StackMapTable -> {
				if (source == 3) {
					// TODO:
					//attributes.add(new StackMapTableAttribute(attributeLength, dis));
				}
				offset = attributeStart + attributeLength;
				continue;
			}
			case Attribute_Exceptions -> {
				if (source == 2) {
					int numberOfExceptions = readUnsignedShort(data, offset);
					offset += 2;
					ClassInfo[] exceptionsTable = new ClassInfo[numberOfExceptions];
					for (int i = 0; i < numberOfExceptions; i++) {
						int index = readUnsignedShort(data, offset);
						offset += 2;
						exceptionsTable[i] = (ClassInfo) constantPool[index - 1];
					}
					attributes.add(new ExceptionsAttribute(exceptionsTable));
				}
				offset = attributeStart + attributeLength;
				continue;
			}
			case Attribute_LineNumberTable -> {
				if (source == 3) {
					int lineNumberTableLength = readUnsignedShort(data, offset);
					offset += 2;
					var lineNumberTable = new LineNumberTableAttribute.LineInfo[lineNumberTableLength];
					for (int i = 0; i < lineNumberTableLength; i++) {
						int startPc = readUnsignedShort(data, offset);
						int lineNumber = readUnsignedShort(data, offset + 2);
						offset += 4;
						lineNumberTable[i] = new LineNumberTableAttribute.LineInfo(startPc, lineNumber);
					}
					attributes.add(new LineNumberTableAttribute(lineNumberTable));
				}
				offset = attributeStart + attributeLength;
				continue;
			}
			case Attribute_LocalVariableTable -> {
				if (source == 3) {
					int localVariableTableLength = readUnsignedShort(data, offset);
					offset += 2;
					var localVariableTable = new LocalVariableTableAttribute.LocalVariableTable[localVariableTableLength];
					for (int i = 0; i < localVariableTableLength; i++) {
						int startPc = readUnsignedShort(data, offset);
						int length = readUnsignedShort(data, offset);
						int nameIndex = readUnsignedShort(data, offset);
						int descriptorIndex = readUnsignedShort(data, offset);
						int index = readUnsignedShort(data, offset);
						offset += 10;
						localVariableTable[i] = new LocalVariableTableAttribute.LocalVariableTable(startPc, length, nameIndex, descriptorIndex, index);
					}
					attributes.add(new LocalVariableTableAttribute(localVariableTable));
				}
				offset = attributeStart + attributeLength;
				continue;
			}
			case Attribute_SourceFile -> {
				if (source == 0) {
					var sourceFile = ((UtfInfo) constantPool[readUnsignedShort(data, offset) - 1]).get();
					attributes.add(new SourceFileAttribute(sourceFile));
				}
				offset = attributeStart + attributeLength;
				continue;
			}
			case Attribute_BootstrapMethods -> {
				if (source == 0) {
					int numBootstrapMethods = readUnsignedShort(data, offset);
					offset += 2;
					var bootstrapMethods = new BootstrapMethodsAttribute.BootstrapMethod[numBootstrapMethods];
					for (int i = 0; i < numBootstrapMethods; i++) {
						int bootstrapMethodRef = readUnsignedShort(data, offset);
						MethodHandleInfo methodHandleInfo = (MethodHandleInfo) constantPool[bootstrapMethodRef - 1];
						int numBootstrapArguments = readUnsignedShort(data, offset + 2);
						offset += 4;
						Object[] bootstrapArguments = new Object[numBootstrapArguments];
						for (int j = 0; j < numBootstrapArguments; j++) {
							int index = readUnsignedShort(data, offset);
							offset += 2;
							bootstrapArguments[j] = constantPool[index - 1];
						}
						bootstrapMethods[i] = new BootstrapMethodsAttribute.BootstrapMethod(methodHandleInfo, bootstrapArguments);
					}
					attributes.add(new BootstrapMethodsAttribute(bootstrapMethods));
				}
				offset = attributeStart + attributeLength;
				continue;
			}
			case Attribute_ConstantValue -> {
				if (source == 1) {
					var constantValue = constantPool[readUnsignedShort(data, offset) - 1];
					attributes.add(new ConstantValueAttribute(constantValue));
				}
				offset = attributeStart + attributeLength;
				continue;
			}
			}
			offset = attributeStart + attributeLength;
		}
		_attrib_out_offset = offset;
		return attributes;
	}
	
	private int parseConstantPool(int offset) {
		byte[] data = this.data;
		for (int i = 0; i < constantPoolCount - 1; i++) {
			int tag = readUnsignedByte(data, offset);
			offset += 1;
			switch (tag) {
			case CONSTANT_Class:
				constantPool[i] = new ClassInfo(readUnsignedShort(data, offset))
					.resolve(constantPool);
				offset += 2;
				break;
			case CONSTANT_Double:
				constantPool[i] = readDouble(data, offset);
				offset += 8;
				break;
			case CONSTANT_Float:
				constantPool[i] = readFloat(data, offset);
				offset += 4;
				break;
			case CONSTANT_Integer:
				constantPool[i] = readInt(data, offset);
				offset += 4;
				break;
			case CONSTANT_Fieldref:
			case CONSTANT_Methodref:
			case CONSTANT_InterfaceMethodref:
				int classIndex = readUnsignedShort(data, offset);
				int nameAndTypeIndex = readUnsignedShort(data, offset + 2);
				constantPool[i] = new RefInfo(classIndex, nameAndTypeIndex)
					.resolve(constantPool);
				offset += 4;
				break;
			case CONSTANT_InvokeDynamic:
				int bootstrapMethodAttrIndex = readUnsignedShort(data, offset);
				nameAndTypeIndex = readUnsignedShort(data, offset + 2);
				constantPool[i] = new InvokeDynamicInfo(bootstrapMethodAttrIndex, nameAndTypeIndex)
					.resolve(constantPool);
				offset += 4;
				break;
			case CONSTANT_Long:
				constantPool[i] = readLong(data, offset);
				offset += 8;
				break;
			case CONSTANT_MethodHandle:
				int referenceKind = readUnsignedByte(data, offset);
				int referenceIndex = readUnsignedShort(data, offset + 1);
				constantPool[i] = new MethodHandleInfo(referenceKind, referenceIndex)
					.resolve(constantPool);
				offset += 3;
				break;
			case CONSTANT_MethodType:
				int descriptorIndex = readUnsignedShort(data, offset);
				constantPool[i] = new MethodTypeInfo(descriptorIndex)
					.resolve(constantPool);
				offset += 2;
				break;
			case CONSTANT_NameAndType:
				int nameIndex = readUnsignedShort(data, offset);
				descriptorIndex = readUnsignedShort(data, offset + 2);
				constantPool[i] = new NameAndTypeInfo(nameIndex, descriptorIndex)
					.resolve(constantPool);
				offset += 4;
				break;
			case CONSTANT_String:
				int stringIndex = readUnsignedShort(data, offset);
				constantPool[i] = new StringInfo(stringIndex);
				offset += 2;
				break;
			case CONSTANT_Utf8:
				constantPool[i] = new UtfInfo(data, offset);
				int utflen = readUnsignedShort(data, offset);
				offset += 2 + utflen;
				break;
			case CONSTANT_Dynamic:
				bootstrapMethodAttrIndex = readUnsignedShort(data, offset);
				nameAndTypeIndex = readUnsignedShort(data, offset + 2);
				constantPool[i] = new DynamicInfo(bootstrapMethodAttrIndex, nameAndTypeIndex)
					.resolve(constantPool);
				offset += 4;
				break;
			default:
				throw new ClassFormatError("bad constant pool tag value %d".formatted(tag));
			}
		}
		
		// Also resolve backwards
		for (int i = constantPool.length - 1; i >= 0; i--) {
			Object constant = constantPool[i];
			if (constant instanceof ClassInfo) {
				ClassInfo classInfo = (ClassInfo) constant;
				constantPool[i] = classInfo.resolve(constantPool);
			} else if (constant instanceof RefInfo) {
				RefInfo refInfo = (RefInfo) constant;
				constantPool[i] = refInfo.resolve(constantPool);
			} else if (constant instanceof InvokeDynamicInfo) {
				InvokeDynamicInfo invokeDynamicInfo = (InvokeDynamicInfo) constant;
				constantPool[i] = invokeDynamicInfo.resolve(constantPool);
			} else if (constant instanceof MethodHandleInfo) {
				MethodHandleInfo methodHandleInfo = (MethodHandleInfo) constant;
				constantPool[i] = methodHandleInfo.resolve(constantPool);
			} else if (constant instanceof MethodTypeInfo) {
				MethodTypeInfo methodTypeInfo = (MethodTypeInfo) constant;
				constantPool[i] = methodTypeInfo.resolve(constantPool);
			} else if (constant instanceof NameAndTypeInfo) {
				NameAndTypeInfo nameAndTypeInfo = (NameAndTypeInfo) constant;
				constantPool[i] = nameAndTypeInfo.resolve(constantPool);
			} else if (constant instanceof DynamicInfo) {
				DynamicInfo dynamicInfo = (DynamicInfo) constant;
				constantPool[i] = dynamicInfo.resolve(constantPool);
			}
		}
		
		return offset;
	}
}
