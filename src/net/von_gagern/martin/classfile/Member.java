package net.von_gagern.martin.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Member implements AttributeOwner {

    private int accessFlags;

    Constant.Utf8 name;

    Constant.Utf8 rawDescriptor;

    List<Attribute> attributes;

    ClassFile cf;

    Member(DataInput in, ClassFile cf) throws IOException {
        this.cf = cf;
        accessFlags = in.readUnsignedShort();
        name = (Constant.Utf8)cf.readConstant(in);
        rawDescriptor = (Constant.Utf8)cf.readConstant(in);
        attributes = cf.readAttributes(in, this);
    }

    public AttributeOwner getParent() {
        return cf;
    }

    public ClassFile getClassFile() {
        return cf;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public String getName() {
        return name.toString();
    }

}
