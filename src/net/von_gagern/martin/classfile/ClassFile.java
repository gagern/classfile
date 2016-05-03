package net.von_gagern.martin.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClassFile implements AttributeOwner {

    private static final int MAGIC = 0xCAFEBABE;

    int minorVersion;

    int majorVersion;

    List<Constant> constantPool;

    int accessFlags;

    Constant.Class thisClass;

    Constant.Class superClass;

    List<Constant.Class> interfaces;

    List<Field> fields;

    List<Method> methods;

    List<Attribute> attributes;

    public ClassFile(ByteBuffer in) throws IOException {
        this(new BufferDataInput(in));
    }

    public ClassFile(DataInput in) throws IOException {
        int magic = in.readInt();
        if (magic != MAGIC)
            throw new IOException("Not a Java class file");
        minorVersion = in.readUnsignedShort();
        majorVersion = in.readUnsignedShort();
        int count;

        count = in.readUnsignedShort();
        Constant[] constantPool = new Constant[count + 1];
        constantPool[0] = Constant.INVALID;
        for (int i = 1; i < count; ++i) {
            Constant c = constantPool[i] = Constant.read(in);
            if (c.doubleSize())
                constantPool[++i] = Constant.INVALID;
        }
        for (int i = 0; i < count; ++i)
            constantPool[i].link(constantPool);
        this.constantPool = unmodifiableList(constantPool);

        accessFlags = in.readUnsignedShort();
        thisClass = (Constant.Class)readConstant(in);
        superClass = (Constant.Class)readConstant(in);

        count = in.readUnsignedShort();
        Constant.Class[] interfaces = new Constant.Class[count];
        for (int i = 0; i < count; ++i) {
            interfaces[i] = (Constant.Class)readConstant(in);
        }
        this.interfaces = unmodifiableList(interfaces);

        count = in.readUnsignedShort();
        Field[] fields = new Field[count];
        for (int i = 0; i < count; ++i) {
            fields[i] = new Field(in, this);
        }
        this.fields = unmodifiableList(fields);

        count = in.readUnsignedShort();
        Method[] methods = new Method[count];
        for (int i = 0; i < count; ++i) {
            methods[i] = new Method(in, this);
        }
        this.methods = unmodifiableList(methods);

        attributes = readAttributes(in, this);
    }

    Constant readConstant(DataInput in) throws IOException {
        int idx = in.readUnsignedShort();
        if (idx == 0) return null;
        return constantPool.get(idx);
    }

    static <T> List<T> unmodifiableList(T[] a) {
        return Collections.unmodifiableList(Arrays.asList(a));
    }

    public Constant getConstant(int idx) {
        return constantPool.get(idx);
    }

    public List<Constant> getConstantPool() {
        return constantPool;
    }

    public Constant.Class getThisClass() {
        return thisClass;
    }

    public String getClassName() {
        return thisClass.toString().replace('/', '.');
    }

    public Constant.Class getSuperClass() {
        return superClass;
    }

    public String getSuperClassName() {
        return superClass.toString().replace('/', '.');
    }

    public List<Constant.Class> getInterfaces() {
        return interfaces;
    }

    public List<String> getInterfaceNames() {
        String[] names = new String[interfaces.size()];
        for (int i = 0; i < interfaces.size(); ++i)
            names[i] = interfaces.get(i).toString().replace('/', '.');
        return unmodifiableList(names);
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    List<Attribute> readAttributes(DataInput in, AttributeOwner owner)
        throws IOException
    {
        int n = in.readUnsignedShort();
        Attribute[] a = new Attribute[n];
        for (int i = 0; i < n; ++i) {
            a[i] = readAttribute(in, owner);
        }
        return unmodifiableList(a);
    }

    Attribute readAttribute(DataInput in, AttributeOwner owner)
        throws IOException
    {
        String name = ((Constant.Utf8)readConstant(in)).toString();
        int length = in.readInt();
        ByteBuffer buf = null;
        if (skipAttribute(name)) {
            int skipped = in.skipBytes(length);
            if (skipped != length)
                throw new IOException("Could not completely skip attribute");
        } else {
            buf = BufferDataInput.readBuffer(in, length);
            if (CodeAttribute.ATTRIBUTE_NAME.equals(name)) {
                return new CodeAttribute(buf, owner);
            }
        }
        return new Attribute(name, buf, owner);
    }

    public String toString() {
        return thisClass.toString();
    }

    boolean skipAttribute(String name) {
        return false;
    }

    // AttributeOwner interface

    public AttributeOwner getParent() {
        return null;
    }

    public ClassFile getClassFile() {
        return this;
    }

}
