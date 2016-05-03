package net.von_gagern.martin.classfile;

import java.io.DataInput;
import java.io.IOException;

public class Field extends Member {

    Descriptor.Value descriptor;

    Field(DataInput in, ClassFile cf) throws IOException {
        super(in, cf);
        descriptor = (Descriptor.Value)Descriptor.parse(rawDescriptor);
    }

    public String getTypeName() {
        return descriptor.getName();
    }

    public Descriptor.Value getDescriptor() {
        return descriptor;
    }

}
