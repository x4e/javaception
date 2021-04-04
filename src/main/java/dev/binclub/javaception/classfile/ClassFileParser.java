package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classfile.constants.*;
import dev.binclub.javaception.classloader.KlassLoader;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static dev.binclub.javaception.classfile.ClassFileConstants.*;
import static dev.binclub.javaception.utils.ByteUtils.*;
import static dev.binclub.javaception.utils.ByteUtils.readUnsignedShort;

public class ClassFileParser {
	public static Klass parse(InputStream stream, InstanceOop loader) throws IOException, ClassNotFoundException {
		return new ClassFileParser(stream.readAllBytes(), loader).toKlass();
	}
	
	public static Klass parse(ByteBuffer buffer, InstanceOop loader) throws ClassNotFoundException {
		return new ClassFileParser(buffer.array(), loader).toKlass();
	}
	
	public static Klass parse(byte[] data, InstanceOop loader) throws ClassNotFoundException {
		return new ClassFileParser(data, loader).toKlass();
	}
	
	
	private static final int FIRST_SUPPORTED_VERSION = V1_2;
	private static final int LAST_SUPPORTED_VERSION = V16;
	
	private final byte[] data;
	private final InstanceOop loader;
	
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
	
	public String sourceFile;
	public BootstrapMethod[] bootstrapMethods;
	
	public Klass toKlass() {
		return new Klass(
			loader,
			constantPool,
			className,
			superClass,
			interfaces,
			fields,
			methods
		);
	}
	
	private ClassFileParser(byte[] data, InstanceOop loader) throws ClassNotFoundException {
		this(data, 0, loader);
	}
	
	private ClassFileParser(byte[] data, int startOffset, InstanceOop loader) throws ClassNotFoundException {
		this.data = data;
		this.loader = loader;
		
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
		int accessOffset = parseConstantPool(startOffset + 10);
		
		access = readUnsignedShort(data, accessOffset);
		
		int classNameIndex = readUnsignedShort(data, accessOffset + 2);
		className = ((ClassInfo) constantPool[classNameIndex - 1]).name;
		int superClassNameIndex = readUnsignedShort(data, accessOffset + 4);
		superClass = KlassLoader.loadClass(loader, ((ClassInfo) constantPool[superClassNameIndex - 1]).name);
		
		int interfacesCount = readUnsignedShort(data, accessOffset + 6);
		interfaces = new Klass[interfacesCount];
		int fieldsOffset = accessOffset + 8;
		for (int i = 0; i < interfacesCount; i++) {
			interfaces[i] = KlassLoader.loadClass(loader, (String) constantPool[readUnsignedShort(data, fieldsOffset) - 1]);
			fieldsOffset += 2;
		}
		
		if ((access & ACC_MODULE) != 0) {
			throw new NoClassDefFoundError("%s is not a class because access_flag ACC_MODULE is set".formatted(className));
		}
		
		int fieldsCount = readUnsignedShort(data, fieldsOffset);
		fields = new FieldInfo[fieldsCount];
		int methodOffset = fieldsOffset + 2;
		for (int i = 0; i < fieldsCount; i++) {
			int access = readUnsignedShort(data, methodOffset);
			int nameIndex = readUnsignedShort(data, methodOffset + 2);
			String name = ((UtfInfo) constantPool[nameIndex - 1]).get();
			int descriptorIndex = readUnsignedShort(data, methodOffset + 4);
			String descriptor = ((UtfInfo) constantPool[descriptorIndex - 1]).get();
			var field = new FieldInfo(access, name, descriptor);
			methodOffset = readFieldAttributes(methodOffset + 6, constantPool, field);
			fields[i] = field;
		}
		
		int methodsCount = readUnsignedShort(data, methodOffset);
		methods = new MethodInfo[methodsCount];
		int attributesOffset = methodOffset + 2;
		for (int i = 0; i < methodsCount; i++) {
			int access = readUnsignedShort(data, attributesOffset);
			int nameIndex = readUnsignedShort(data, attributesOffset + 2);
			String name = ((UtfInfo) constantPool[nameIndex - 1]).get();
			int descriptorIndex = readUnsignedShort(data, attributesOffset + 4);
			String descriptor = ((UtfInfo) constantPool[descriptorIndex - 1]).get();
			var methodInfo = new MethodInfo(access, name, descriptor);
			attributesOffset = readMethodAttributes(attributesOffset + 6, constantPool, methodInfo);
			methods[i] = methodInfo;
		}
		
		readClassAttributes(attributesOffset, constantPool);
	}
	
	private int readFieldAttributes(int offset, Object[] constantPool, FieldInfo field) {
		int attributesCount = readUnsignedShort(data, offset);
		offset += 2;
		while (attributesCount-- > 0) {
			int attributeNameIndex = readUnsignedShort(data, offset);
			int attributeLength = readInt(data, offset + 2);
			offset += 6;
			int attributeStart = offset;
			
			String name = ((UtfInfo) constantPool[attributeNameIndex - 1]).get();
			switch (name) {
			case Attribute_ConstantValue -> field.constantValue = constantPool[readUnsignedShort(data, offset) - 1];
			}
			
			offset = attributeStart + attributeLength;
		}
		return offset;
	}
	
	private int readMethodAttributes(int offset, Object[] constantPool, MethodInfo method) {
		int attributesCount = readUnsignedShort(data, offset);
		offset += 2;
		while (attributesCount-- > 0) {
			int attributeNameIndex = readUnsignedShort(data, offset);
			int attributeLength = readInt(data, offset + 2);
			offset += 6;
			int attributeStart = offset;
			
			String name = ((UtfInfo) constantPool[attributeNameIndex - 1]).get();
			switch (name) {
			case Attribute_Code -> {
				method.code = new CodeAttribute(data, offset, constantPool);
			}}
			
			offset = attributeStart + attributeLength;
		}
		return offset;
	}
	
	private int readClassAttributes(int offset, Object[] constantPool) {
		int attributesCount = readUnsignedShort(data, offset);
		offset += 2;
		while (attributesCount-- > 0) {
			int attributeNameIndex = readUnsignedShort(data, offset);
			int attributeLength = readInt(data, offset + 2);
			offset += 6;
			int attributeStart = offset;
			
			String name = ((UtfInfo) constantPool[attributeNameIndex - 1]).get();
			switch (name) {
			case Attribute_SourceFile -> {
				sourceFile = ((UtfInfo) constantPool[readUnsignedShort(data, offset) - 1]).get();
			}
			case Attribute_BootstrapMethods -> {
				int numBootstrapMethods = readUnsignedShort(data, offset);
				offset += 2;
				bootstrapMethods = new BootstrapMethod[numBootstrapMethods];
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
					bootstrapMethods[i] = new BootstrapMethod(methodHandleInfo, bootstrapArguments);
				}
			}}
			
			offset = attributeStart + attributeLength;
		}
		return offset;
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
	
	public static class BootstrapMethod {
		MethodHandleInfo bootstrapMethodRef;
		Object[] bootstrapArguments;
		
		public BootstrapMethod(MethodHandleInfo bootstrapMethodRef, Object[] bootstrapArguments) {
			this.bootstrapMethodRef = bootstrapMethodRef;
			this.bootstrapArguments = bootstrapArguments;
		}
	}
}
