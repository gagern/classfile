package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class InnerClass {

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

}
