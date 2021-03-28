package dev.binclub.javaception.classfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import dev.binclub.javaception.classfile.attributes.CodeAttribute;
import dev.binclub.javaception.classfile.attributes.ExceptionsAttribute;
import dev.binclub.javaception.classfile.attributes.LineNumberTableAttribute;
import dev.binclub.javaception.classfile.attributes.LocalVariableTableAttribute;
import dev.binclub.javaception.classfile.attributes.SourceFileAttribute;
import dev.binclub.javaception.classfile.attributes.StackMapTableAttribute;

public class ClassFileParser {

	// for testing purposes
	public static void main(String[] args) throws Throwable {
		InputStream stream = ClassFileParser.class.getClassLoader().getResourceAsStream("klass/ClassReader.class");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int available = 0;
		while ((available = stream.available()) != 0) {
			byte[] bytes = new byte[available];
			stream.read(bytes);
			bos.write(bytes);
		}
		new ClassFileParser(bos.toByteArray());

	}

	// the value of the tag byte acts as the index to get the type
	public static final ConstantTypes[] types = new ConstantTypes[19];

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
	Object[] constantPool;
	ClassInfoCP[] interfaces;
	FieldInfo[] fields;
	MethodInfo[] methods;
	AttributeInfo[] attributes;

	public ClassFileParser(byte[] classBytes) throws Throwable {
		if (classBytes.length < 4) {
			// need atleast 4 bytes get magic number
			throw new Throwable("Invalid class file");
		}

		parseClass(classBytes);

	}

	public void parseClass(byte[] classBytes) throws Throwable {

		dis = new DataInputStream(new ByteArrayInputStream(classBytes));
		int magic = dis.readInt();
		if (magic != 0xCAFEBABE) {
			throw new Throwable("Invalid Class File");
		}
		minorVersion = dis.readUnsignedShort();
		majorVersion = dis.readUnsignedShort();
		constantPoolCount = dis.readUnsignedShort();
		constantPool = new Object[constantPoolCount - 1];
		// force enum to resolve
		System.out.println(ConstantTypes.values().length);
		parseConstantPool();
		access = dis.readUnsignedShort();
		int classNameIndex = dis.readUnsignedShort();
		className = ((ClassInfo) constantPool[classNameIndex - 1]).getClassName(constantPool);
		System.out.println("ClassName " + className);
		int superClassNameIndex = dis.readUnsignedShort();
		superClass = ((ClassInfo) constantPool[superClassNameIndex - 1]).getClassName(constantPool);
		System.out.println("SuperClass " + superClass);
		interfacesCount = dis.readUnsignedShort();
		getInterfaces();
		fieldsCount = dis.readUnsignedShort();
		readFields();
		methodsCount = dis.readUnsignedShort();
		readMethods();
		attributesCount = dis.readUnsignedShort();
		if (attributesCount != 0) {
			attributes = new AttributeInfo[attributesCount];
			for (int i = 0; i < attributesCount; i++) {
				attributes[i] = readAttribute(dis, constantPool);
			}
		}
	}

	public void readMethods() throws IOException {
		methods = new MethodInfo[methodsCount];
		for (int i = 0; i < methodsCount; i++) {
			int access = dis.readUnsignedShort();
			int nameIndex = dis.readUnsignedShort();
			int descriptorIndex = dis.readUnsignedShort();
			int attributesCount = dis.readUnsignedShort();
			AttributeInfo[] attributes = null;
			if (attributesCount != 0) {
				attributes = new AttributeInfo[attributesCount];
				for (int j = 0; j < attributesCount; j++) {
					attributes[j] = readAttribute(dis, constantPool);
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
			AttributeInfo[] attributes = null;
			if (attributesCount != 0) {
				attributes = new AttributeInfo[attributesCount];
				for (int j = 0; j < attributesCount; j++) {
					attributes[j] = readAttribute(dis, constantPool);
				}
			}
			fields[i] = new FieldInfo(access, nameIndex, descriptorIndex, attributes);
		}
	}

	public static AttributeInfo readAttribute(DataInputStream dis, Object[] constantPool) throws IOException {
		int attributeNameIndex = dis.readUnsignedShort();
		int attributeLength = dis.readInt();
		String name = (String) constantPool[attributeNameIndex - 1];
		switch (name) {
		case "Code":
			return new CodeAttribute(attributeNameIndex, attributeLength, dis, constantPool);
		case "StackMapTable":
			return new StackMapTableAttribute(attributeNameIndex, attributeLength, dis);
		case "Exceptions":
			return new ExceptionsAttribute(attributeNameIndex, attributeLength, dis, constantPool);
		case "LineNumberTable":
			return new LineNumberTableAttribute(attributeNameIndex, attributeLength, dis);
		case "LocalVariableTable":
			return new LocalVariableTableAttribute(attributeNameIndex, attributeLength, dis);
		case "SourceFile":
			return new SourceFileAttribute(attributeNameIndex, attributeLength, dis);
		default:
			System.out.println("skipping attrib type " + name);
			for (int i = 0; i < attributeLength; i++) {
				dis.readUnsignedByte();
			}
			break;
		}
		return null;
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
				dis.readInt();
				constantPool[i] = type;
				break;
			case FLOAT:
				constantPool[i] = dis.readFloat();
				break;
			case INTEGER:
				constantPool[i] = dis.readInt();
				break;
			case INTERFACEMETHODREF:
				dis.readInt();
				constantPool[i] = type;
				break;
			case INVOKEDYNAMIC:
				dis.readInt();
				constantPool[i] = type;
				break;
			case LONG:
				constantPool[i] = dis.readLong();
				break;
			case METHODHANDLE:
				dis.readUnsignedByte();
				dis.readUnsignedShort();
				constantPool[i] = type;
				break;
			case METHODREF:
				dis.readInt();
				constantPool[i] = type;
				break;
			case METHODTYPE:
				dis.readUnsignedShort();
				constantPool[i] = type;
				break;
			case NAMEANDTYPE:
				dis.readInt();
				constantPool[i] = type;
				break;
			case STRING:
				dis.readUnsignedShort();
				constantPool[i] = type;
				break;
			case UTF8:
				constantPool[i] = dis.readUTF();
				break;
			default:
				System.out.println("Unsupported constant type : " + tag);
				break;

			}
		}

	}

}
