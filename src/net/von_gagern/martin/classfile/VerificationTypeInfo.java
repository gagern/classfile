package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class VerificationTypeInfo implements ClassWriter.Writable {

    public static final VerificationTypeInfo TOP =
        new VerificationTypeInfo("top", null);

    public static final VerificationTypeInfo ONE_WORD =
        new VerificationTypeInfo("oneWord", TOP);

    public static final VerificationTypeInfo TWO_WORD =
        new VerificationTypeInfo("twoWord", TOP);

    public static final VerificationTypeInfo INTEGER =
        new VerificationTypeInfo("int", ONE_WORD);

    public static final VerificationTypeInfo FLOAT =
        new VerificationTypeInfo("float", ONE_WORD);

    public static final VerificationTypeInfo LONG =
        new VerificationTypeInfo("long", TWO_WORD);

    public static final VerificationTypeInfo DOUBLE =
        new VerificationTypeInfo("double", TWO_WORD);

    public static final VerificationTypeInfo REFERENCE =
        new VerificationTypeInfo("reference", ONE_WORD);
    
    public static final VerificationTypeInfo NULL =
        new VerificationTypeInfo("null", REFERENCE);

    public static final VerificationTypeInfo UNINITIALIZED =
        new VerificationTypeInfo("uninitialized", REFERENCE);
    
    public static final VerificationTypeInfo UNINITIALIZED_THIS =
        new VerificationTypeInfo("uninitializedThis", UNINITIALIZED);
    
    private String name;

    private Constant.Class type;

    private int offset;

    CodeLabel offsetLabel;

    private VerificationTypeInfo parent;

    private VerificationTypeInfo(String name, VerificationTypeInfo parent) {
        this.name = name;
        this.parent = parent;
    }

    private VerificationTypeInfo(Constant.Class type) {
        this.type = type;
        this.parent = REFERENCE;
    }

    private VerificationTypeInfo(int offset) {
        this.offset = offset;
        this.parent = UNINITIALIZED;
    }

    @Override public String toString() {
        if (name != null) return name;
        if (type != null) return type.toString();
        return "Uninitialized(" + offset + ")";
    }

    public static VerificationTypeInfo forObject(Constant.Class type) {
        return new VerificationTypeInfo(type);
    }

    public static VerificationTypeInfo uninitialized(int offset) {
        return new VerificationTypeInfo(offset);
    }

    private static final List<VerificationTypeInfo> NO_ARG_TYPES = Arrays.asList
        (TOP, INTEGER, FLOAT, DOUBLE, LONG, NULL, UNINITIALIZED_THIS);

    static VerificationTypeInfo parse(ByteBuffer buf, ClassFile cf) {
        int tag = buf.get() & 0xff;
        switch(tag) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
            return NO_ARG_TYPES.get(tag);
        case 7:
            int cpool_index = buf.getShort() & 0xffff;
            Constant.Class c = (Constant.Class)cf.getConstant(cpool_index);
            return forObject(c);
        case 8:
            return uninitialized(buf.getShort() & 0xffff);
        default:
            throw new IllegalArgumentException("Invalid tag: " + tag);
        }
    }

    public void writeTo(ClassWriter w) {
        if (type != null) {
            w.writeU1(7);
            w.write2(type);
            return;
        } else if (name != null) {
            w.writeU1(NO_ARG_TYPES.indexOf(this));
        } else {
            w.writeU1(8);
            w.linkOffset2(offsetLabel, 0);
        }
    }

    boolean isUninitializedWithOffset() {
        return type == null && name == null;
    }

    int getUninitializedOffset() {
        return offset;
    }

}
