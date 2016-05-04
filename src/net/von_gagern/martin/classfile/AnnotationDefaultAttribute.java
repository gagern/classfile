package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class AnnotationDefaultAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "AnnotationDefault";

    public AnnotationDefaultAttribute
        (ByteBuffer content, AttributeOwner owner)
    {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public AnnotationValue getValue() {
        return Annotation.readValue(contentBuffer(), owner.getClassFile());
    }

    @Override public void writeContent(ClassWriter w) {
        w.write(getValue());
    }

}
