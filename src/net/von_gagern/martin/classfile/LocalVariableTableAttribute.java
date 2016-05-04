package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.List;

public class LocalVariableTableAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "LocalVariableTable";

    public LocalVariableTableAttribute
        (ByteBuffer content, AttributeOwner owner)
    {
        super(ATTRIBUTE_NAME, content, owner);
    }

    private List<LocalVariableInfo.WithDescriptor> cached;

    public List<LocalVariableInfo.WithDescriptor> getTable() {
        if (cached != null) return cached;
        ClassFile cf = owner.getClassFile();
        ByteBuffer buf = contentBuffer();
        int n = buf.getShort() & 0xffff;
        LocalVariableInfo.WithDescriptor[] table =
            new LocalVariableInfo.WithDescriptor[n];
        for (int i = 0; i < n; ++i)
            table[i] = new LocalVariableInfo.WithDescriptor(buf, cf);
        return cached = ClassFile.unmodifiableList(table);
    }

    @Override public void writeContent(ClassWriter w) {
        List<LocalVariableInfo.WithDescriptor> table = getTable();
        w.writeU2(table.size());
        for (LocalVariableInfo.WithDescriptor itm: table)
            w.write(itm);
    }

}
