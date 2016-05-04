package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class InnerClass implements ClassWriter.Writable {

    Constant.Class innerClass;
    Constant.Class outerClass;
    Constant.Utf8 innerName;
    AccessFlags innerAccess;

    InnerClass(ByteBuffer buf, ClassFile cf) {
        innerClass = (Constant.Class)cf.getConstant(buf.getShort() & 0xffff);
        outerClass = (Constant.Class)cf.getConstant(buf.getShort() & 0xffff);
        innerName = (Constant.Utf8)cf.getConstant(buf.getShort() & 0xffff);
        innerAccess = AccessFlags.innerClassFlags(buf.getShort() & 0xffff);
    }

    public void writeTo(ClassWriter w) {
        w.write2(innerClass);
        w.write2(outerClass);
        w.write2(innerName);
        w.writeU2(innerAccess.bitPattern());
    }

}
