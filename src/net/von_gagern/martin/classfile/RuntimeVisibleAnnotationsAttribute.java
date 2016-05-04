package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class RuntimeVisibleAnnotationsAttribute extends Attribute
    implements Iterable<Annotation>
{

    public static final String ATTRIBUTE_NAME = "RuntimeVisibleAnnotations";

    public RuntimeVisibleAnnotationsAttribute
        (ByteBuffer content, AttributeOwner owner)
    {
        super(ATTRIBUTE_NAME, content, owner);
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
