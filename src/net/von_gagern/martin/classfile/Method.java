package net.von_gagern.martin.classfile;

import java.io.DataInput;
import java.io.IOException;

public class Method extends Member {

    Descriptor.Method descriptor;

    Method(DataInput in, ClassFile cf) throws IOException {
        super(AccessFlags.methodFlags(in.readUnsignedShort()), in, cf);
        descriptor = (Descriptor.Method)Descriptor.parse(rawDescriptor);
    }

    public CodeAttribute getCode() {
        for (Attribute a: attributes)
            if (a instanceof CodeAttribute &&
                CodeAttribute.ATTRIBUTE_NAME.equals(a.name))
                return ((CodeAttribute)a);
        return null;
    }

    public Descriptor.Method getDescriptor() {
        return descriptor;
    }

}
