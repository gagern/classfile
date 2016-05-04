package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.List;

public class StackMapTableAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "StackMapTable";

    public StackMapTableAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public List<StackMapFrame> getFrames() {
        ClassFile cf = owner.getClassFile();
        ByteBuffer buf = contentBuffer();
        int n = buf.getShort() & 0xffff;
        StackMapFrame[] frames = new StackMapFrame[n];
        for (int i = 0; i < n; ++i) {
            frames[i] = StackMapFrame.parse(buf, cf);
        }
        return ClassFile.unmodifiableList(frames);
    }

}
