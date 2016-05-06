package net.von_gagern.martin.classfile;

import java.util.Locale;
import java.nio.ByteBuffer;

class ExceptionTableEntry implements ClassWriter.Writable {

    int startAddress;
    int endAddress;
    int handlerAddress;
    CodeLabel start;
    CodeLabel end;
    CodeLabel handler;
    Constant.Class catchType;

    ExceptionTableEntry(ByteBuffer buf, ClassFile cf) {
        startAddress = buf.getShort() & 0xffff;
        endAddress = buf.getShort() & 0xffff;
        handlerAddress = buf.getShort() & 0xffff;
        catchType = (Constant.Class)cf.getConstant(buf.getShort() & 0xffff);
    }

    public void writeTo(ClassWriter w) {
        w.linkOffset2(start, 0);
        w.linkOffset2(end, 0);
        w.linkOffset2(handler, 0);
        w.write2(catchType);
    }

}
