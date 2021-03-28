package dev.binclub.javaception.classfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;

public class ClassFileParser {

	// for testing purposes
	public static void main(String[] args) throws Throwable {
		InputStream stream = ClassFileParser.class.getClassLoader()
				.getResourceAsStream("dev/binclub/javaception/classfile/ClassFileParser.class");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int available = 0;
		while ((available = stream.available()) != 0) {
			System.out.println(available);
			byte[] bytes = new byte[available];
			stream.read(bytes);
			bos.write(bytes);
		}
		new ClassFileParser(bos.toByteArray());

	}

	// the value of the tag byte acts as the index to get the type
	public static ConstantTypes[] types = new ConstantTypes[19];

	int constantPoolCount;
	int majorVersion;
	int minorVersion;
	int access;
	String className;
	String superClass;
	DataInputStream dis;
	Object[] constantPool;

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
		System.out.println(ConstantTypes.values().length);
		parseConstantPool();
		access = dis.readUnsignedShort();
		int classNameIndex = dis.readUnsignedShort();
		className = (String) constantPool[classNameIndex];
		int superClassNameIndex = dis.readUnsignedShort();
		superClass = (String) constantPool[superClassNameIndex];
		System.out.println(superClass);

	}

	public void parseConstantPool() throws Throwable {

		for (int i = 0; i < constantPoolCount - 1; i++) {
			int tag = dis.readUnsignedByte();
			ConstantTypes type = types[tag];
			switch (type) {
			case CLASS:
				constantPool[i] = new ClassInfo(dis.readUnsignedShort(), constantPool);
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
				break;

			}
		}
		//this can now be called now that referenced indexs are resolved
		for(Object obj : constantPool) {
			if(obj instanceof CpInitializable) {
				CpInitializable<?> entry = (CpInitializable<?>) obj;
				entry.initialize();
			}
		}

	}

}
