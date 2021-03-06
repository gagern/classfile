package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class RuntimeVisibleAnnotationsAttribute
    extends AbstractAnnotationsAttribute
{

    public static final String ATTRIBUTE_NAME = "RuntimeVisibleAnnotations";

    public RuntimeVisibleAnnotationsAttribute
        (ByteBuffer content, AttributeOwner owner)
    {
        super(ATTRIBUTE_NAME, content, owner);
    }

}
