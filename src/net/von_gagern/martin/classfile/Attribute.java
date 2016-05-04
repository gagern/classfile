package net.von_gagern.martin.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Attribute implements ClassWriter.Writable {

    public final String name;

    private ByteBuffer buf;

    AttributeOwner owner;

    protected Attribute(String name, ByteBuffer content, AttributeOwner owner) {
        this.name = name;
        this.buf = content;
        this.owner = owner;
    }

    public ByteBuffer contentBuffer() {
        return buf.asReadOnlyBuffer();
    }

    public void writeTo(ClassWriter w) {
        w.write2(new Constant.Utf8(name));
        ClassWriter.Deferred count = w.deferU4();
        writeContent(w);
        count.writeByteCount();
    }

    public void writeContent(ClassWriter w) {
        w.write(buf);
    }

    public boolean isUnderstood() {
        return true;
    }

}
