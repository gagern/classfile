package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

class LookupSwitchOp extends SwitchOp {

    int[] matches;

    LookupSwitchOp(ByteBuffer buf) {
        super(OpCode.LOOKUPSWITCH, buf.position() - 1);
        buf.position((opCodePos + 4) & (~3));
        offset = buf.getInt();
        int n = buf.getInt();
        if (n < 0)
            throw new IllegalArgumentException("negative case count");
        matches = new int[n];
        offsets = new int[n];
        for (int i = 0; i < n; ++i) {
            matches[i] = buf.getInt();
            offsets[i] = buf.getInt();
        }
    }

    public int getMatch(int idx) {
        return matches[idx];
    }

    @Override public void writeTo(ClassWriter w) {
        int base = w.posInCode();
        w.write(code);
        w.align4();
        w.linkOffset4(target, base);
        int n = matches.length;
        w.writeU4(n);
        for (int i = 0; i < n; ++i) {
            w.writeI4(matches[i]);
            w.linkOffset4(targets.get(i), base);
        }
    }

}
