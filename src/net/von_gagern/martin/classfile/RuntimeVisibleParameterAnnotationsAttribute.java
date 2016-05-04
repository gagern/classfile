package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

class RuntimeVisibleParameterAnnotationsAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME =
        "RuntimeVisibleParameterAnnotations";
    
    public RuntimeVisibleParameterAnnotationsAttribute
        (ByteBuffer content, AttributeOwner owner)
    {
        super(ATTRIBUTE_NAME, content, owner);
    }

}
