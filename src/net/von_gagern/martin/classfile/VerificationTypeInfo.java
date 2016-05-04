package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public class VerificationTypeInfo {

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

    static VerificationTypeInfo parse(ByteBuffer buf, ClassFile cf) {
        int tag = buf.get() & 0xff;
        switch(tag) {
        case 0: return TOP;
        case 1: return INTEGER;
        case 2: return FLOAT;
        case 3: return DOUBLE;
        case 4: return LONG;
        case 5: return NULL;
        case 6: return UNINITIALIZED_THIS;
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

}
