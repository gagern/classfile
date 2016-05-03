package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Iterator;

class CodeIterator implements Iterator<Op> {

    private ByteBuffer buf;

    public CodeIterator(ByteBuffer buf) {
        this.buf = buf;
    }

    public boolean hasNext() {
        return buf.hasRemaining();
    }

    public void remove() {
        throw new UnsupportedOperationException("code is read-only");
    }

    public Op next() {
        OpCode c = OpCode.forByte(buf.get());
        return c.args.makeOp(c, buf);
    }

}
