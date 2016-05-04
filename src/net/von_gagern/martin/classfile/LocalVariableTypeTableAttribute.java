package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.List;

public class LocalVariableTypeTableAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "LocalVariableTypeTable";

    public LocalVariableTypeTableAttribute
        (ByteBuffer content, AttributeOwner owner)
    {
        super(ATTRIBUTE_NAME, content, owner);
    }

    private List<LocalVariableInfo.WithSignature> cached;

    public List<LocalVariableInfo.WithSignature> getTable() {
        if (cached != null) return cached;
        ClassFile cf = owner.getClassFile();
        ByteBuffer buf = contentBuffer();
        int n = buf.getShort() & 0xffff;
        LocalVariableInfo.WithSignature[] table =
            new LocalVariableInfo.WithSignature[n];
        for (int i = 0; i < n; ++i)
            table[i] = new LocalVariableInfo.WithSignature(buf, cf);
        return cached = ClassFile.unmodifiableList(table);
    }

    @Override public void writeContent(ClassWriter w) {
        List<LocalVariableInfo.WithSignature> table = getTable();
        w.writeU2(table.size());
        for (LocalVariableInfo.WithSignature itm: table)
            w.write(itm);
    }

}
