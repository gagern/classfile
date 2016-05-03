package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

class TableSwitchOp extends SwitchOp {

    int low;

    int high;

    TableSwitchOp(ByteBuffer buf) {
        super(OpCode.TABLESWITCH, buf.position() - 1);
        buf.position((opCodePos + 4) & (~3));
        offset = buf.getInt();
        low = buf.getInt();
        high = buf.getInt();
        if (low > high)
            throw new IllegalArgumentException("inverted table switch range");
        int n = high - low + 1;
        offsets = new int[n];
        for (int i = 0; i < n; ++i)
            offsets[i] = buf.getInt();
        numBytes = buf.position() - opCodePos;
    }

    public int getMatch(int idx) {
        return low + idx;
    }

}
