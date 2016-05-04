package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class SourceFileAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "SourceFile";

    public SourceFileAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public Constant.Utf8 getNameConstant() {
        int idx = contentBuffer().getShort(0) & 0xffff;
        return (Constant.Utf8)owner.getClassFile().getConstant(idx);
    }

}
