package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class ConstantValueAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "ConstantValue";

    public ConstantValueAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public Constant getValue() {
        int idx = contentBuffer().getShort(0) & 0xffff;
        return owner.getClassFile().getConstant(idx);
    }

}
