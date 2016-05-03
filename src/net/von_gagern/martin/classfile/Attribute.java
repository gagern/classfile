package net.von_gagern.martin.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Attribute {

    public final String name;

    private ByteBuffer buf;

    AttributeOwner owner;

    public Attribute(String name, ByteBuffer content, AttributeOwner owner) {
        this.name = name;
        this.buf = content;
        this.owner = owner;
    }

    public ByteBuffer contentBuffer() {
        return buf.asReadOnlyBuffer();
    }

}
