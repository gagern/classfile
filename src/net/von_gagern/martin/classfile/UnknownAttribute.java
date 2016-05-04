package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class UnknownAttribute extends Attribute {

    public UnknownAttribute
        (String name, ByteBuffer content, AttributeOwner owner)
    {
        super(name, content, owner);
    }

    public boolean isUnderstood() {
        return false;
    }

}
