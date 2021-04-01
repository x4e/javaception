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

import static dev.binclub.javaception.classfile.ClassFileConstants.*;

public class ClassFileParser {
	private static final int FIRST_SUPPORTED_VERSION = V1_2;
	private static final int LAST_SUPPORTED_VERSION = V16;
	
	public final DataInputStream dis;
	
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
		superClass = KlassLoader.loadClass(loader, ((ClassInfo) constantPool[superClassNameIndex - 1]).getClassName());
		
		int interfacesCount = dis.readUnsignedShort();
		interfaces = new Klass[interfacesCount];
		for (int i = 0; i < interfacesCount; i++) {
			interfaces[i] = KlassLoader.loadClass(loader, (String) constantPool[dis.readUnsignedShort() - 1]);
		}
		
		if ((access & ACC_MODULE) != 0) {
			throw new NoClassDefFoundError("%s is not a class because access_flag ACC_MODULE is set".formatted(className));
		}
		
		int fieldsCount = dis.readUnsignedShort();
		fields = new FieldInfo[fieldsCount];
		for (int i = 0; i < fieldsCount; i++) {
			int access = dis.readUnsignedShort();
			int nameIndex = dis.readUnsignedShort();
			int descriptorIndex = dis.readUnsignedShort();
			List<AttributeInfo> attributes = readAttributes(dis, constantPool, 1);
			fields[i] = new FieldInfo(access, nameIndex, descriptorIndex, attributes);
		}
		
		int methodsCount = dis.readUnsignedShort();
		methods = new MethodInfo[methodsCount];
		for (int i = 0; i < methodsCount; i++) {
			int access = dis.readUnsignedShort();
			int nameIndex = dis.readUnsignedShort();
			int descriptorIndex = dis.readUnsignedShort();
			List<AttributeInfo> attributes = readAttributes(dis, constantPool, 2);
			methods[i] = new MethodInfo(access, nameIndex, descriptorIndex, attributes);
			
		}
		
		attributes = readAttributes(dis, constantPool, 0);
	}
	
	/**
	 * @param source 0 = class, 1 = field, 2 = method, 3 = code
	 */
	public static List<AttributeInfo> readAttributes(DataInputStream dis, Object[] constantPool, int source) throws IOException {
		int attributesCount = dis.readUnsignedShort();
		ArrayList<AttributeInfo> attributes = new ArrayList<>(attributesCount);
		for (int i = 0; i < attributesCount; i++) {
			int attributeNameIndex = dis.readUnsignedShort();
			int attributeLength = dis.readInt();
			String name = (String) constantPool[attributeNameIndex - 1];
			
			switch (name) {
			case Attribute_Code:
				if (source == 2) {
					attributes.add(new CodeAttribute(dis, constantPool));
				}
				break;
			case Attribute_StackMapTable:
				if (source == 3) {
					attributes.add(new StackMapTableAttribute(attributeLength, dis));
				}
				break;
			case Attribute_Exceptions:
				if (source == 3) {
					attributes.add(new ExceptionsAttribute(dis, constantPool));
				}
				break;
			case Attribute_LineNumberTable:
				if (source == 3) {
					attributes.add(new LineNumberTableAttribute(dis));
				}
				break;
			case Attribute_LocalVariableTable:
				if (source == 3) {
					attributes.add(new LocalVariableTableAttribute(dis));
				}
				break;
			case Attribute_SourceFile:
				if (source == 0) {
					attributes.add(new SourceFileAttribute(dis, constantPool));
				}
				break;
			case Attribute_BootstrapMethods:
				if (source == 0) {
					attributes.add(new BootstrapMethodsAttribute(dis, constantPool));
				}
				break;
			case Attribute_ConstantValue:
				if (source == 1) {
					attributes.add(new ConstantValueAttribute(dis, constantPool));
				}
				break;
			default:
				dis.skipBytes(attributeLength);
				break;
			}
		}
		return attributes;
	}
	
	private void parseConstantPool() throws Throwable {
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
				throw new ClassFormatError("bad constant pool tag value %d".formatted(tag));
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
				invokeDynamicInfo.nameAndTypeInfo = (NameAndTypeInfo) constantPool[invokeDynamicInfo.nameAndTypeIndex - 1];
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
}
