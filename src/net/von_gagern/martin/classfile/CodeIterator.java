package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Iterator;

class CodeIterator implements Iterator<Op> {

    private ByteBuffer buf;

    private ClassFile cf;

    public CodeIterator(ByteBuffer buf, ClassFile cf) {
        this.buf = buf;
        this.cf = cf;
    }

    public boolean hasNext() {
        return buf.hasRemaining();
    }

    public void remove() {
        throw new UnsupportedOperationException("code is read-only");
    }

    public Op next() {
        OpCode c = OpCode.forByte(buf.get());
        return c.args.makeOp(c, buf, cf);
    }

}
