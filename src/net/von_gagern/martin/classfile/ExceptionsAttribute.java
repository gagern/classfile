package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class ExceptionsAttribute extends Attribute
    implements Iterable<Constant.Class>
{

    public static final String ATTRIBUTE_NAME = "Exceptions";

    public ExceptionsAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public List<Constant.Class> getExceptions() {
        ClassFile cf = owner.getClassFile();
        ByteBuffer buf = contentBuffer();
        int n = buf.getShort() & 0xffff;
        Constant.Class[] exceptions = new Constant.Class[n];
        for (int i = 0; i < n; ++i)
            exceptions[i] =
                (Constant.Class)cf.getConstant(buf.getShort() & 0xffff);
        return ClassFile.unmodifiableList(exceptions);
    }

    public Iterator<Constant.Class> iterator() {
        return getExceptions().iterator();
    }

    @Override public void writeContent(ClassWriter w) {
        List<Constant.Class> exceptions = getExceptions();
        w.writeU2(exceptions.size());
        for (Constant.Class exception: exceptions)
            w.write2(exception);
    }

}
