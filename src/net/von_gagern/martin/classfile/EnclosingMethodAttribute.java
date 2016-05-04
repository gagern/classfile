package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class EnclosingMethodAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "EnclosingMethod";

    public EnclosingMethodAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public Constant.Class getClazz() {
        int idx = contentBuffer().getShort(0) & 0xffff;
        return (Constant.Class)owner.getClassFile().getConstant(idx);
    }

    public Constant.NameAndType getMethod() {
        int idx = contentBuffer().getShort(2) & 0xffff;
        return (Constant.NameAndType)owner.getClassFile().getConstant(idx);
    }

    public Constant.Methodref getMethodref() {
        ClassFile cf = owner.getClassFile();
        ByteBuffer buf = contentBuffer();
        int ci = buf.getShort() & 0xffff;
        int nti = buf.getShort() & 0xffff;
        Constant.Class c = (Constant.Class)cf.getConstant(ci);
        Constant.NameAndType nt = (Constant.NameAndType)cf.getConstant(nti);
        return new Constant.Methodref(c, nt);
    }

}
