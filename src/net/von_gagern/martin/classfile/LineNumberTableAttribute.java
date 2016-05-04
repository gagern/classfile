package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

public class LineNumberTableAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "LineNumberTable";

    public LineNumberTableAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public NavigableMap<Integer, Integer> getAddressMap() {
        NavigableMap<Integer, Integer> map = new TreeMap<>();
        ByteBuffer buf = contentBuffer();
        int n = buf.getShort() & 0xffff;
        for (int i = 0; i < n; ++i) {
            int startPC = buf.getShort() & 0xffff;
            int lineNumber = buf.getShort() & 0xffff;
            map.put(startPC, lineNumber);
        }
        return Collections.unmodifiableNavigableMap(map);
    }

}
