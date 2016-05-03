package net.von_gagern.martin.classfile;

import java.util.Locale;
import java.nio.ByteBuffer;

class ExceptionTableEntry {

    int start;
    int end;
    int handler;
    Constant.Class catchType;

    ExceptionTableEntry(ByteBuffer buf, ClassFile cf, String where) {
        start = buf.getShort() & 0xffff;
        end = buf.getShort() & 0xffff;
        handler = buf.getShort() & 0xffff;
        int ct = buf.getShort() & 0xffff;
        if (ct != 0)
            catchType = (Constant.Class)cf.constantPool.get(ct);
    }

}
