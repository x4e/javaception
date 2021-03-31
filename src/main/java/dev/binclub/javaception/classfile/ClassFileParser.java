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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClassFileParser {
	
	// the value of the tag byte acts as the index to get the type
	public static final ConstantTypes[] types = ConstantTypes.values();
	// public for testing
	public Object[] constantPool;
	// public for testing
	public MethodInfo[] methods;
	int constantPoolCount;
	int majorVersion;
	int minorVersion;
	int access;
	int interfacesCount;
	int fieldsCount;
	int methodsCount;
	int attributesCount;
	String className;
	String superClass;
	DataInputStream dis;
	ClassInfoCP[] interfaces;
	FieldInfo[] fields;
	AttributeInfo[] attributes;
	
	public ClassFileParser(byte[] classBytes) throws Throwable {
		if (classBytes.length < 4) {
			// need atleast 4 bytes get magic number
			throw new ClassFormatError();
		}
		
		parseClass(classBytes);
		
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
	
	public void parseClass(byte[] classBytes) throws Throwable {
		dis = new DataInputStream(new ByteArrayInputStream(classBytes));
		int magic = dis.readInt();
		if (magic != 0xCAFEBABE) {
			throw new ClassFormatError();
		}
		minorVersion = dis.readUnsignedShort();
		majorVersion = dis.readUnsignedShort();
		constantPoolCount = dis.readUnsignedShort();
		constantPool = new Object[constantPoolCount - 1];
		// force enum to resolve
		ConstantTypes.values();
		parseConstantPool();
		access = dis.readUnsignedShort();
		int classNameIndex = dis.readUnsignedShort();
		className = ((ClassInfo) constantPool[classNameIndex - 1]).getClassName();
		int superClassNameIndex = dis.readUnsignedShort();
		superClass = ((ClassInfo) constantPool[superClassNameIndex - 1]).getClassName();
		interfacesCount = dis.readUnsignedShort();
		getInterfaces();
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
	
	public void getInterfaces() throws Throwable {
		interfaces = new ClassInfoCP[interfacesCount];
		for (int i = 0; i < interfacesCount; i++) {
			interfaces[i] = (ClassInfoCP) constantPool[dis.readUnsignedShort() - 1];
		}
	}
	
	public void parseConstantPool() throws Throwable {
		
		for (int i = 0; i < constantPoolCount - 1; i++) {
			int tag = dis.readUnsignedByte();
			ConstantTypes type = types[tag];
			switch (type) {
			case CLASS:
				constantPool[i] = new ClassInfo(dis.readUnsignedShort());
				break;
			case DOUBLE:
				constantPool[i] = dis.readDouble();
				break;
			case FIELDREF:
				int classIndex = dis.readUnsignedShort();
				int nameAndTypeIndex = dis.readUnsignedShort();
				constantPool[i] = new RefInfo(classIndex, nameAndTypeIndex);
				break;
			case FLOAT:
				constantPool[i] = dis.readFloat();
				break;
			case INTEGER:
				constantPool[i] = dis.readInt();
				break;
			case INTERFACEMETHODREF:
				classIndex = dis.readUnsignedShort();
				nameAndTypeIndex = dis.readUnsignedShort();
				constantPool[i] = new RefInfo(classIndex, nameAndTypeIndex);
				break;
			case INVOKEDYNAMIC:
				int bootstrapMethodAttrIndex = dis.readUnsignedShort();
				nameAndTypeIndex = dis.readUnsignedShort();
				constantPool[i] = new InvokeDynamicInfo(bootstrapMethodAttrIndex, nameAndTypeIndex);
				break;
			case LONG:
				constantPool[i] = dis.readLong();
				break;
			case METHODHANDLE:
				int referenceKind = dis.readUnsignedByte();
				int referenceIndex = dis.readUnsignedShort();
				constantPool[i] = new MethodHandleInfo(referenceKind, referenceIndex);
				break;
			case METHODREF:
				classIndex = dis.readUnsignedShort();
				nameAndTypeIndex = dis.readUnsignedShort();
				constantPool[i] = new RefInfo(classIndex, nameAndTypeIndex);
				break;
			case METHODTYPE:
				int descriptorIndex = dis.readUnsignedShort();
				constantPool[i] = new MethodTypeInfo(descriptorIndex);
				break;
			case NAMEANDTYPE:
				int nameIndex = dis.readUnsignedShort();
				descriptorIndex = dis.readUnsignedShort();
				constantPool[i] = new NameAndTypeInfo(nameIndex, descriptorIndex);
				break;
			case STRING:
				int stringIndex = dis.readUnsignedShort();
				constantPool[i] = new UnresolvedString(stringIndex);
				break;
			case UTF8:
				constantPool[i] = dis.readUTF();
				break;
			case DYNAMIC:
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
