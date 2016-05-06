package net.von_gagern.martin.classfile;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

abstract class SwitchOp extends BranchOp {

    protected SwitchOp(OpCode code, int opCodePos) {
        super(code, opCodePos, 0);
    }

    int[] offsets;

    List<CodeLabel> targets;

    @Override public String formatArgs(String indent) {
        Formatter buf = new Formatter((Locale)null);
        buf.format("{\n");
        int numCases = getNumCases();
        for (int i = 0; i < numCases; ++i) {
            int match = getMatch(i);
            int target = opCodePos + getOffset(i);
            buf.format("%s%11d: %5d\n", indent, match, target);
        }
        buf.format("%s    default: %5d\n%s}", indent,
                   opCodePos + this.offset, indent);
        return buf.toString();
    }

    public int getNumCases() {
        return offsets.length;
    }

    public abstract int getMatch(int idx);

    public int getOffset(int idx) {
        return offsets[idx];
    }

}
