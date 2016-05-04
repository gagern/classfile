package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class DeprecatedAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "Deprecated";

    public DeprecatedAttribute(ByteBuffer content, AttributeOwner owner)
    {
        super(ATTRIBUTE_NAME, content, owner);
    }

    @Override public void writeContent(ClassWriter w) {
    }

}
