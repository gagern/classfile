package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public abstract class LocalVariableInfo implements ClassWriter.Writable {

    int startPC;
    int length;
    Constant.Utf8 name;
    Constant.Utf8 descriptorOrSignature;
    int index;

    LocalVariableInfo(ByteBuffer buf, ClassFile cf) {
        startPC = buf.getShort() & 0xffff;
        length = buf.getShort() & 0xffff;
        name = (Constant.Utf8)cf.getConstant(buf.getShort() & 0xffff);
        descriptorOrSignature =
            (Constant.Utf8)cf.getConstant(buf.getShort() & 0xffff);
        index = buf.getShort() & 0xffff;
    }

    LocalVariableInfo(int index) {
        this.index = index;
    }

    public void writeTo(ClassWriter w) {
        w.writeU2(startPC);
        w.writeU2(length);
        w.write2(name);
        w.write2(descriptorOrSignature);
        w.writeU2(index);
    }

    public int getIndex() {
        return index;
    }

    public static class WithDescriptor extends LocalVariableInfo {
        WithDescriptor(ByteBuffer buf, ClassFile cf) {
            super(buf, cf);
        }
    }

    public static class WithSignature extends LocalVariableInfo {
        WithSignature(ByteBuffer buf, ClassFile cf) {
            super(buf, cf);
        }
    }

    public static class IndexOnly extends LocalVariableInfo {
        public IndexOnly(int index) {
            super(index);
        }
    }

}
