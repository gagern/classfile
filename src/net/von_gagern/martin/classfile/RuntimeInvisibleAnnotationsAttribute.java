package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class RuntimeInvisibleAnnotationsAttribute
    extends AbstractAnnotationsAttribute
{

    public static final String ATTRIBUTE_NAME = "RuntimeInvisibleAnnotations";

    public RuntimeInvisibleAnnotationsAttribute
        (ByteBuffer content, AttributeOwner owner)
    {
        super(ATTRIBUTE_NAME, content, owner);
    }

}
