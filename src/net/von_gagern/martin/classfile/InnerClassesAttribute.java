package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class InnerClassesAttribute extends Attribute
    implements Iterable<InnerClass>
{

    public static final String ATTRIBUTE_NAME = "InnerClasses";

    public InnerClassesAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    private List<InnerClass> cached;

    public List<InnerClass> getInnerClasses() {
        if (cached != null) return cached;
        ClassFile cf = owner.getClassFile();
        ByteBuffer buf = contentBuffer();
        int n = buf.getShort() & 0xffff;
        InnerClass[] innerClasses = new InnerClass[n];
        for (int i = 0; i < n; ++i)
            innerClasses[i] = new InnerClass(buf, cf);
        return cached = ClassFile.unmodifiableList(innerClasses);
    }

    public Iterator<InnerClass> iterator() {
        return getInnerClasses().iterator();
    }

}
