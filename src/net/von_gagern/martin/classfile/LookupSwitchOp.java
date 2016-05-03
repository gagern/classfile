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
        numBytes = buf.position() - opCodePos;
    }

    public int getMatch(int idx) {
        return matches[idx];
    }

}
