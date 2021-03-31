package dev.binclub.javaception.classfile;

import dev.binclub.javaception.classfile.attributes.*;
import dev.binclub.javaception.classfile.constants.ClassInfo;
import dev.binclub.javaception.classfile.constants.DynamicInfo;
import dev.binclub.javaception.classfile.constants.InvokeDynamicInfo;
import dev.binclub.javaception.classfile.constants.MethodHandleInfo;
import dev.binclub.javaception.classfile.constants.MethodTypeInfo;
import dev.binclub.javaception.classfile.constants.NameAndTypeInfo;
import dev.binclub.javaception.classfile.constants.RefInfo;
import dev.binclub.javaception.classfile.constants.UnresolvedString;
import dev.binclub.javaception.classloader.KlassLoader;
import dev.binclub.javaception.klass.Klass;
import dev.binclub.javaception.oop.InstanceOop;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static dev.binclub.javaception.classfile.ClassFileConstants.*;

public class ClassFileParser {
	private static final int FIRST_SUPPORTED_VERSION = V1_2;
	private static final int LAST_SUPPORTED_VERSION = V16;
	
	// public for testing
	public Object[] constantPool;
	// public for testing
	public MethodInfo[] methods;
	int constantPoolCount;
	int majorVersion;
	int minorVersion;
	int access;
	int fieldsCount;
	int methodsCount;
	int attributesCount;
	String className;
	String superClass;
	DataInputStream dis;
	Klass[] interfaces;
	FieldInfo[] fields;
	AttributeInfo[] attributes;
	
	public ClassFileParser(byte[] classBytes, InstanceOop loader) throws Throwable {
		dis = new DataInputStream(new ByteArrayInputStream(classBytes));
		
		if (dis.readInt() != CLASS_MAGIC) {
			throw new ClassFormatError();
		}
		
		minorVersion = dis.readUnsignedShort();
		majorVersion = dis.readUnsignedShort();
		if (majorVersion > LAST_SUPPORTED_VERSION || majorVersion < FIRST_SUPPORTED_VERSION) {
			throw new UnsupportedClassVersionError("Unsupported class file version: " + majorVersion + "." + minorVersion);
		}
		
		constantPoolCount = dis.readUnsignedShort();
		constantPool = new Object[constantPoolCount - 1];
		parseConstantPool();
		
		access = dis.readUnsignedShort();
		
		int classNameIndex = dis.readUnsignedShort();
		className = ((ClassInfo) constantPool[classNameIndex - 1]).getClassName();
		int superClassNameIndex = dis.readUnsignedShort();
		superClass = ((ClassInfo) constantPool[superClassNameIndex - 1]).getClassName();
		
		int interfacesCount = dis.readUnsignedShort();
		interfaces = new Klass[interfacesCount];
		for (int i = 0; i < interfacesCount; i++) {
			interfaces[i] = KlassLoader.loadClass(loader, (String) constantPool[dis.readUnsignedShort() - 1]);
		}
		
		if ((access & ACC_MODULE) != 0) {
			throw new NoClassDefFoundError("%s is not a class because access_flag ACC_MODULE is set".formatted(className));
		}
		
		
		fieldsCount = dis.readUnsignedShort();
		readFields();
		methodsCount = dis.readUnsignedShort();
		readMethods();
		attributesCount = dis.readUnsignedShort();
		attributes = new AttributeInfo[attributesCount];
		for (int i = 0; i < attributesCount; i++) {
			attributes[i] = readAttribute(dis, constantPool);
		}
	}
	
	public static AttributeInfo readAttribute(DataInputStream dis, Object[] constantPool) throws IOException {
		int attributeNameIndex = dis.readUnsignedShort();
		int attributeLength = dis.readInt();
		String name = (String) constantPool[attributeNameIndex - 1];
		switch (name) {
		case "Code":
			return new CodeAttribute(attributeLength, dis, constantPool);
		case "StackMapTable":
			return new StackMapTableAttribute(attributeLength, dis);
		case "Exceptions":
			return new ExceptionsAttribute(attributeLength, dis, constantPool);
		case "LineNumberTable":
			return new LineNumberTableAttribute(attributeLength, dis);
		case "LocalVariableTable":
			return new LocalVariableTableAttribute(attributeLength, dis);
		case "SourceFile":
			return new SourceFileAttribute(attributeLength, dis);
		case "BootstrapMethods":
			return new BootstrapMethodsAttribute(attributeLength, dis, constantPool);
		default:
			System.out.println("skipping attrib type " + name);
			dis.skipBytes(attributeLength);
			break;
		}
		return null;
	}
	
	//stuff for testing
	public static int addTest(int a, int b) {
		return a + b;
	}
	
	public static String appendTest(String text) {
		return "hello " + text;
	}
	
	public void readMethods() throws IOException {
		methods = new MethodInfo[methodsCount];
		for (int i = 0; i < methodsCount; i++) {
			int access = dis.readUnsignedShort();
			int nameIndex = dis.readUnsignedShort();
			int descriptorIndex = dis.readUnsignedShort();
			int attributesCount = dis.readUnsignedShort();
			List<AttributeInfo> attributes = new ArrayList<>();
			if (attributesCount != 0) {
				for (int j = 0; j < attributesCount; j++) {
					AttributeInfo attribute = readAttribute(dis, constantPool);
					if (attribute != null) {
						attributes.add(attribute);
					}
				}
			}
			methods[i] = new MethodInfo(access, nameIndex, descriptorIndex, attributes);
			
		}
	}
	
	public void readFields() throws Throwable {
		fields = new FieldInfo[fieldsCount];
		for (int i = 0; i < fieldsCount; i++) {
			int access = dis.readUnsignedShort();
			int nameIndex = dis.readUnsignedShort();
			int descriptorIndex = dis.readUnsignedShort();
			int attributesCount = dis.readUnsignedShort();
			List<AttributeInfo> attributes = new ArrayList<>();
			if (attributesCount != 0) {
				for (int j = 0; j < attributesCount; j++) {
					AttributeInfo attribute = readAttribute(dis, constantPool);
					if (attribute != null) {
						attributes.add(attribute);
					}
				}
			}
			fields[i] = new FieldInfo(access, nameIndex, descriptorIndex, attributes);
		}
	}
	
	public void parseConstantPool() throws Throwable {
		
		for (int i = 0; i < constantPoolCount - 1; i++) {
			int tag = dis.readUnsignedByte();
			switch (tag) {
			case CONSTANT_Class:
				constantPool[i] = new ClassInfo(dis.readUnsignedShort());
				break;
			case CONSTANT_Double:
				constantPool[i] = dis.readDouble();
				break;
			case CONSTANT_Fieldref:
				int classIndex = dis.readUnsignedShort();
				int nameAndTypeIndex = dis.readUnsignedShort();
				constantPool[i] = new RefInfo(classIndex, nameAndTypeIndex);
				break;
			case CONSTANT_Float:
				constantPool[i] = dis.readFloat();
				break;
			case CONSTANT_Integer:
				constantPool[i] = dis.readInt();
				break;
			case CONSTANT_Methodref:
			case CONSTANT_InterfaceMethodref:
				classIndex = dis.readUnsignedShort();
				nameAndTypeIndex = dis.readUnsignedShort();
				constantPool[i] = new RefInfo(classIndex, nameAndTypeIndex);
				break;
			case CONSTANT_InvokeDynamic:
				int bootstrapMethodAttrIndex = dis.readUnsignedShort();
				nameAndTypeIndex = dis.readUnsignedShort();
				constantPool[i] = new InvokeDynamicInfo(bootstrapMethodAttrIndex, nameAndTypeIndex);
				break;
			case CONSTANT_Long:
				constantPool[i] = dis.readLong();
				break;
			case CONSTANT_MethodHandle:
				int referenceKind = dis.readUnsignedByte();
				int referenceIndex = dis.readUnsignedShort();
				constantPool[i] = new MethodHandleInfo(referenceKind, referenceIndex);
				break;
			case CONSTANT_MethodType:
				int descriptorIndex = dis.readUnsignedShort();
				constantPool[i] = new MethodTypeInfo(descriptorIndex);
				break;
			case CONSTANT_NameAndType:
				int nameIndex = dis.readUnsignedShort();
				descriptorIndex = dis.readUnsignedShort();
				constantPool[i] = new NameAndTypeInfo(nameIndex, descriptorIndex);
				break;
			case CONSTANT_String:
				int stringIndex = dis.readUnsignedShort();
				constantPool[i] = new UnresolvedString(stringIndex);
				break;
			case CONSTANT_Utf8:
				constantPool[i] = dis.readUTF();
				break;
			case CONSTANT_Dynamic:
				bootstrapMethodAttrIndex = dis.readUnsignedShort();
				nameAndTypeIndex = dis.readUnsignedShort();
				constantPool[i] = new DynamicInfo(bootstrapMethodAttrIndex, nameAndTypeIndex);
				break;
			default:
				throw new ClassFormatError("Unsupported constant type");
				
			}
		}
		// make sure a string can always be referenced this avoids nullptr references
		for (int i = 0; i < constantPool.length; i++) {
			Object constant = constantPool[i];
			if (constant instanceof UnresolvedString) {
				UnresolvedString us = (UnresolvedString) constant;
				constantPool[i] = constantPool[us.stringIndex - 1];
				
			}
		}
		// resolves
		for (int i = 0; i < constantPool.length; i++) {
			Object constant = constantPool[i];
			if (constant instanceof ClassInfo) {
				ClassInfo classInfo = (ClassInfo) constant;
				classInfo.name = (String) constantPool[classInfo.nameIndex - 1];
			}
			if (constant instanceof NameAndTypeInfo) {
				NameAndTypeInfo nameAndTypeInfo = (NameAndTypeInfo) constant;
				nameAndTypeInfo.name = (String) constantPool[nameAndTypeInfo.nameIndex - 1];
				nameAndTypeInfo.description = (String) constantPool[nameAndTypeInfo.descriptorIndex - 1];
			}
			if (constant instanceof RefInfo) {
				RefInfo refInfo = (RefInfo) constant;
				refInfo.classInfo = (ClassInfo) constantPool[refInfo.classIndex - 1];
				refInfo.nameAndTypeInfo = (NameAndTypeInfo) constantPool[refInfo.nameAndTypeIndex - 1];
			}
			if (constant instanceof DynamicInfo) {
				DynamicInfo dynamicInfo = (DynamicInfo) constant;
				dynamicInfo.nameAndTypeInfo = (NameAndTypeInfo) constantPool[dynamicInfo.nameAndTypeIndex - 1];
			}
			if (constant instanceof InvokeDynamicInfo) {
				InvokeDynamicInfo invokeDynamicInfo = (InvokeDynamicInfo) constant;
				invokeDynamicInfo.nameAndTypeInfo = (NameAndTypeInfo) constantPool[invokeDynamicInfo.nameAndTypeIndex
					- 1];
			}
			if (constant instanceof MethodHandleInfo) {
				MethodHandleInfo methodHandleInfo = (MethodHandleInfo) constant;
				methodHandleInfo.refInfo = (RefInfo) constantPool[methodHandleInfo.referenceIndex - 1];
			}
			if (constant instanceof MethodTypeInfo) {
				MethodTypeInfo methodTypeInfo = (MethodTypeInfo) constant;
				methodTypeInfo.methodDescription = (String) constantPool[methodTypeInfo.descriptorIndex - 1];
			}
			
		}
		
	}
	
	public Supplier<String> getSupplier(String s) {
		return () -> {
			return s;
		};
	}
	
}
