package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

class RuntimeInvisibleParameterAnnotationsAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME =
        "RuntimeInvisibleParameterAnnotations";
    
    public RuntimeInvisibleParameterAnnotationsAttribute
        (ByteBuffer content, AttributeOwner owner)
    {
        super(ATTRIBUTE_NAME, content, owner);
    }

}
