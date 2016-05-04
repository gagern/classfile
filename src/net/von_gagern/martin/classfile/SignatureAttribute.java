package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class SignatureAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "Signature";

    public SignatureAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public Constant.Utf8 getSignatureConstant() {
        int idx = contentBuffer().getShort(0) & 0xffff;
        return (Constant.Utf8)owner.getClassFile().getConstant(idx);
    }

}
