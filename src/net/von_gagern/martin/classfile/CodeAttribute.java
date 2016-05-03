package net.von_gagern.martin.classfile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class CodeAttribute extends Attribute
    implements AttributeOwner, Iterable<Op>
{

    public static final String ATTRIBUTE_NAME = "Code";

    int maxStack;

    int maxLocals;

    ByteBuffer code;

    ExceptionTableEntry[] exceptionTable;

    List<Attribute> attributes;

    public CodeAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public void parse() {
        ClassFile cf = owner.getClassFile();
        ByteBuffer content = contentBuffer();
        String where = cf + "." + ((Method)owner).name;
        maxStack = content.getShort() & 0xffff;
        maxLocals = content.getShort() & 0xffff;
        int count = content.getInt();
        int oldLimit = content.limit();
        int codeEnd = content.position() + count;
        content.limit(codeEnd);
        code = content.slice();
        content.limit(oldLimit).position(codeEnd);
        count = content.getShort() & 0xffff;
        exceptionTable = new ExceptionTableEntry[count];
        for (int i = 0; i < count; ++i)
            exceptionTable[i] = new ExceptionTableEntry(content, cf, where);
        try {
            attributes = cf.readAttributes(new BufferDataInput(content), this);
        } catch (IOException e) {
            if (e.getCause() instanceof RuntimeException)
                throw (RuntimeException)e.getCause();
            throw new RuntimeException(e);
        }
    }

    public Iterator<Op> iterator() {
        if (code == null) parse();
        return new CodeIterator(code);
    }

    public AttributeOwner getParent() {
        return owner;
    }

    public ClassFile getClassFile() {
        return owner.getClassFile();
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

}
