package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class AbstractParameterAnnotationsAttribute extends Attribute {

    protected AbstractParameterAnnotationsAttribute
        (String name, ByteBuffer content, AttributeOwner owner)
    {
        super(name, content, owner);
    }

    public List<List<Annotation>> getAnnotations() {
        // Not using arrays in here since doing so conflicts with generics
        ClassFile cf = owner.getClassFile();
        ByteBuffer buf = contentBuffer();
        int m = buf.getShort() & 0xffff;
        List<List<Annotation>> params = new ArrayList<>(m);
        for (int i = 0; i < m; ++i) {
            int n = buf.getShort() & 0xffff;
            if (n == 0) {
                List<Annotation> empty = Collections.emptyList();
                params.add(empty);
                continue;
            }
            List<Annotation> annotations = new ArrayList<>(n);
            for (int j = 0; j < n; ++j)
                annotations.add(new Annotation(buf, cf));
            params.add(Collections.unmodifiableList(annotations));
        }
        return Collections.unmodifiableList(params);
    }

    @Override public void writeContent(ClassWriter w) {
        List<List<Annotation>> params = getAnnotations();
        w.writeU2(params.size());
        for (List<Annotation> annotations: params) {
            w.writeU2(annotations.size());
            for (Annotation annotation: annotations)
                w.write(annotation);
        }
    }

}
