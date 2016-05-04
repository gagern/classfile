package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

abstract class AbstractAnnotationsAttribute extends Attribute
    implements Iterable<Annotation>
{

    protected AbstractAnnotationsAttribute
        (String name, ByteBuffer content, AttributeOwner owner)
    {
        super(name, content, owner);
    }

    public List<Annotation> getAnnotations() {
        ClassFile cf = owner.getClassFile();
        ByteBuffer buf = contentBuffer();
        int n = buf.getShort() & 0xffff;
        Annotation[] annotations = new Annotation[n];
        for (int i = 0; i < n; ++i)
            annotations[i] = new Annotation(buf, cf);
        return ClassFile.unmodifiableList(annotations);
    }

    public Iterator<Annotation> iterator() {
        return getAnnotations().iterator();
    }

    @Override public void writeContent(ClassWriter w) {
        List<Annotation> annotations = getAnnotations();
        w.writeU2(annotations.size());
        for (Annotation annotation: annotations)
            w.write(annotation);
    }

}
