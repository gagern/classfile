package net.von_gagern.martin.classfile;

import java.io.DataInput;
import java.io.IOException;

public abstract class Constant {

    static Constant read(DataInput in) throws IOException {
        int tag = in.readByte();
        switch (tag) {
        case Utf8.TAG: return new Utf8(in);
        case Integer.TAG: return new Integer(in);
        case Float.TAG: return new Float(in);
        case Long.TAG: return new Long(in);
        case Double.TAG: return new Double(in);
        case Class.TAG: return new Class(in);
        case String.TAG: return new String(in);
        case Fieldref.TAG: return new Fieldref(in);
        case Methodref.TAG: return new Methodref(in);
        case InterfaceMethodref.TAG: return new InterfaceMethodref(in);
        case NameAndType.TAG: return new NameAndType(in);
        case MethodHandle.TAG: return new MethodHandle(in);
        case MethodType.TAG: return new MethodType(in);
        case CallSiteSpec.TAG: return new CallSiteSpec(in);
        default:
            throw new IOException("Invalid constant tag: " + tag);
        }
    }

    public abstract int getTag();

    public boolean doubleSize() {
        return false;
    }

    void link(Constant[] cp) {
    }

    public static class Class extends Constant {

        public static final int TAG = 7;

        private int nameIndex;

        private Constant.Utf8 name;

        Class(DataInput in) throws IOException {
            nameIndex = in.readUnsignedShort();
        }

        public Class(Constant.Utf8 name) {
            this.name = name;
        }

        public Class(java.lang.String name) {
            this.name = new Constant.Utf8(name.replace('.', '/'));
        }

        @Override void link(Constant[] cp) {
            name = (Constant.Utf8)cp[nameIndex];
        }

        public int getTag() {
            return TAG;
        }

        public Constant.Utf8 getName() {
            return name;
        }

        @Override public java.lang.String toString() {
            return name.toString();
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.Class c = (Constant.Class)o;
            return name.equals(c.name);
        }

        @Override public int hashCode() {
            return name.hashCode() ^ TAG;
        }

    }

    public static abstract class Ref extends Constant {
        
        private int classIndex;
        
        private Constant.Class clazz;

        private int nameAndTypeIndex;

        private Constant.NameAndType nameAndType;

        Ref(DataInput in) throws IOException {
            classIndex = in.readUnsignedShort();
            nameAndTypeIndex = in.readUnsignedShort();
        }

        Ref(Constant.Class clazz, Constant.NameAndType nameAndType) {
            this.clazz = clazz;
            this.nameAndType = nameAndType;
        }

        Ref(java.lang.String clazz, java.lang.String name, java.lang.String descriptor) {
            this.clazz = new Constant.Class(clazz);
            this.nameAndType = new Constant.NameAndType(name, descriptor);
        }

        @Override void link(Constant[] cp) {
            clazz = (Constant.Class)cp[classIndex];
            nameAndType = (Constant.NameAndType)cp[nameAndTypeIndex];
        }

        public Constant.Class getClazz() {
            return clazz;
        }

        public Constant.NameAndType getNameAndType() {
            return nameAndType;
        }

        @Override public java.lang.String toString() {
            return clazz + "." + nameAndType;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.Ref c = (Constant.Ref)o;
            return nameAndType.equals(c.nameAndType) &&
                clazz.equals(c.clazz);
        }

        @Override public int hashCode() {
            return nameAndType.hashCode() ^ (17 * clazz.hashCode());
        }

    }

    public static class Fieldref extends Ref {

        public static final int TAG = 9;

        Fieldref(DataInput in) throws IOException {
            super(in);
        }

        public Fieldref(Constant.Class clazz, Constant.NameAndType nameAndType) {
            super(clazz, nameAndType);
        }

        public Fieldref(java.lang.String clazz, java.lang.String name, java.lang.String descriptor) {
            super(clazz, name, descriptor);
        }

        public int getTag() {
            return TAG;
        }

        @Override public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override public int hashCode() {
            return super.hashCode() ^ TAG;
        }

    }

    public static class Methodref extends Ref {

        public static final int TAG = 10;

        Methodref(DataInput in) throws IOException {
            super(in);
        }

        public Methodref(Constant.Class clazz, Constant.NameAndType nameAndType) {
            super(clazz, nameAndType);
        }

        public Methodref(java.lang.String clazz, java.lang.String name, java.lang.String descriptor) {
            super(clazz, name, descriptor);
        }

        public int getTag() {
            return TAG;
        }

        @Override public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override public int hashCode() {
            return super.hashCode() ^ TAG;
        }

    }

    public static class InterfaceMethodref extends Ref {

        public static final int TAG = 11;

        InterfaceMethodref(DataInput in) throws IOException {
            super(in);
        }

        public InterfaceMethodref(Constant.Class clazz, Constant.NameAndType nameAndType) {
            super(clazz, nameAndType);
        }

        public InterfaceMethodref(java.lang.String clazz, java.lang.String name, java.lang.String descriptor) {
            super(clazz, name, descriptor);
        }

        public int getTag() {
            return TAG;
        }

        @Override public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override public int hashCode() {
            return super.hashCode() ^ TAG;
        }

    }

    public static class String extends Constant {

        public static final int TAG = 8;

        private int stringIndex;

        private Constant.Utf8 string;

        String(DataInput in) throws IOException {
            stringIndex = in.readUnsignedShort();
        }

        public String(Constant.Utf8 string) {
            this.string = string;
        }

        public String(java.lang.String string) {
            this.string = new Constant.Utf8(string);
        }

        @Override void link(Constant[] cp) {
            string = (Constant.Utf8)cp[stringIndex];
        }

        public int getTag() {
            return TAG;
        }

        public Constant.Utf8 getString() {
            return string;
        }

        @Override public java.lang.String toString() {
            return string.toString();
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.String c = (Constant.String)o;
            return string.equals(c.string);
        }

        @Override public int hashCode() {
            return string.hashCode() ^ TAG;
        }

    }

    public static class Integer extends Constant {

        public static final int TAG = 3;

        private int value;

        Integer(DataInput in) throws IOException {
            value = in.readInt();
        }

        public Integer(int value) {
            this.value = value;
        }

        public int getTag() {
            return TAG;
        }

        public int getValue() {
            return value;
        }

        @Override public java.lang.String toString() {
            return java.lang.Integer.toString(value);
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.Integer c = (Constant.Integer)o;
            return value == c.value;
        }

        @Override public int hashCode() {
            return (value * 17) ^ TAG;
        }

    }

    public static class Float extends Constant {

        public static final int TAG = 4;

        private float value;

        Float(DataInput in) throws IOException {
            value = in.readFloat();
        }

        public Float(float value) {
            this.value = value;
        }

        public int getTag() {
            return TAG;
        }

        public float getValue() {
            return value;
        }

        @Override public java.lang.String toString() {
            return java.lang.Float.toString(value);
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.Float c = (Constant.Float)o;
            return value == c.value;
        }

        @Override public int hashCode() {
            return (java.lang.Float.floatToIntBits(value) * 17) ^ TAG;
        }

    }

    public static class Long extends Constant {

        public static final int TAG = 5;

        private long value;

        Long(DataInput in) throws IOException {
            value = in.readLong();
        }

        public Long(long value) {
            this.value = value;
        }

        @Override public boolean doubleSize() {
            return true;
        }

        public int getTag() {
            return TAG;
        }

        public long getValue() {
            return value;
        }

        @Override public java.lang.String toString() {
            return java.lang.Long.toString(value);
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.Long c = (Constant.Long)o;
            return value == c.value;
        }

        @Override public int hashCode() {
            int h = (int)(value ^ (value >>> 32));
            return (h * 17) ^ TAG;
        }

    }

    public static class Double extends Constant {

        public static final int TAG = 6;

        private double value;

        Double(DataInput in) throws IOException {
            value = in.readDouble();
        }

        public Double(double value) {
            this.value = value;
        }

        @Override public boolean doubleSize() {
            return true;
        }

        public int getTag() {
            return TAG;
        }

        public double getValue() {
            return value;
        }

        @Override public java.lang.String toString() {
            return java.lang.Double.toString(value);
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.Double c = (Constant.Double)o;
            return value == c.value;
        }

        @Override public int hashCode() {
            long l = java.lang.Double.doubleToLongBits(value);
            int h = (int)(l ^ (l >>> 32));
            return (h * 17) ^ TAG;
        }

    }

    public static class NameAndType extends Constant {

        public static final int TAG = 12;

        private int nameIndex;

        private Constant.Utf8 name;

        private int descriptorIndex;

        private Constant.Utf8 descriptor;

        NameAndType(DataInput in) throws IOException {
            nameIndex = in.readUnsignedShort();
            descriptorIndex = in.readUnsignedShort();
        }

        public NameAndType(Constant.Utf8 name, Constant.Utf8 descriptor) {
            this.name = name;
            this.descriptor = descriptor;
        }

        public NameAndType(java.lang.String name, java.lang.String descriptor) {
            this.name = new Constant.Utf8(name);
            this.descriptor = new Constant.Utf8(descriptor);
        }

        @Override void link(Constant[] cp) {
            name = (Constant.Utf8)cp[nameIndex];
            descriptor = (Constant.Utf8)cp[descriptorIndex];
        }

        public int getTag() {
            return TAG;
        }

        public Constant.Utf8 getName() {
            return name;
        }

        public Constant.Utf8 getDescriptor() {
            return descriptor;
        }

        @Override public java.lang.String toString() {
            return name + ":" + descriptor;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.NameAndType c = (Constant.NameAndType)o;
            return name.equals(c.name) && descriptor.equals(c.descriptor);
        }

        @Override public int hashCode() {
            return name.hashCode() ^ (17 * descriptor.hashCode());
        }

    }

    public static class Utf8 extends Constant {

        public static final int TAG = 1;

        private java.lang.String value;

        Utf8(DataInput in) throws IOException {
            value = in.readUTF();
        }

        public Utf8(java.lang.String value) {
            this.value = value;
        }

        public int getTag() {
            return TAG;
        }

        public java.lang.String getValue() {
            return value;
        }

        @Override public java.lang.String toString() {
            return value;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.Utf8 c = (Constant.Utf8)o;
            return value.equals(c.value);
        }

        @Override public int hashCode() {
            return value.hashCode() ^ TAG;
        }

    }

    public static class MethodHandle extends Constant {

        public static final int TAG = 15;

        private int kind;

        private int index;

        private Constant.Ref reference;

        MethodHandle(DataInput in) throws IOException {
            kind = in.readUnsignedByte();
            index = in.readUnsignedShort();
        }

        @Override void link(Constant[] cp) {
            reference = (Constant.Ref)cp[index];
        }

        public int getTag() {
            return TAG;
        }

        public int getReferenceKind() {
            return kind;
        }

        public Constant.Ref getReference() {
            return reference;
        }

        @Override public java.lang.String toString() {
            return "Method handle " + kind + " to " + reference;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.MethodHandle c = (Constant.MethodHandle)o;
            return kind == c.kind && reference.equals(c.reference);
        }

        @Override public int hashCode() {
            return reference.hashCode() ^ (17 * kind) ^ TAG;
        }

    }

    public static class MethodType extends Constant {

        public static final int TAG = 16;

        private int descriptorIndex;

        private Constant.Utf8 descriptor;

        MethodType(DataInput in) throws IOException {
            descriptorIndex = in.readUnsignedByte();
        }

        @Override void link(Constant[] cp) {
            descriptor = (Constant.Utf8)cp[descriptorIndex];
        }

        public int getTag() {
            return TAG;
        }

        public Constant.Utf8 getDescriptor() {
            return descriptor;
        }

        @Override public java.lang.String toString() {
            return descriptor.toString();
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            Constant.MethodType c = (Constant.MethodType)o;
            return descriptor.equals(c.descriptor);
        }

        @Override public int hashCode() {
            return descriptor.hashCode() ^ TAG;
        }

    }

    // TODO: Make this public once its name and API stabilizes
    static class CallSiteSpec extends Constant {

        public static final int TAG = 18;

        // TODO: Tie this in with the BootstrapMethods attribute

        private int bootstrapMethodAttrIndex;

        private int nameAndTypeIndex;

        private Constant.NameAndType nameAndType;

        CallSiteSpec(DataInput in) throws IOException {
            bootstrapMethodAttrIndex = in.readUnsignedShort();
            nameAndTypeIndex = in.readUnsignedShort();
        }

        @Override void link(Constant[] cp) {
            nameAndType = (Constant.NameAndType)cp[nameAndTypeIndex];
        }

        public int getTag() {
            return TAG;
        }

        public int getBootstrapMethodAttrIndex() {
            return bootstrapMethodAttrIndex;
        }

        public Constant.NameAndType getNameAndType() {
            return nameAndType;
        }

        @Override public java.lang.String toString() {
            return "Invoke dynamic " + bootstrapMethodAttrIndex +
                "." + nameAndType;
        }

        // This constant type uses Object equality for now

    }

    static class Invalid extends Constant {

        public int getTag() {
            return 0;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !getClass().equals(o.getClass()))
                return false;
            return true;
        }

        @Override public int hashCode() {
            return 0;
        }

    }

    static final Invalid INVALID = new Invalid();

}
