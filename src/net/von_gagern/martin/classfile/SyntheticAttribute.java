package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class SyntheticAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "Synthetic";

    public SyntheticAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

}
