package net.von_gagern.martin.classfile;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public abstract class SwitchOp extends BranchOp {

    protected SwitchOp(OpCode code, int opCodePos) {
        super(code, opCodePos, 0);
    }

    int[] offsets;

    List<CodeLabel> targets;

    public int getNumCases() {
        return offsets.length;
    }

    public abstract int getMatch(int idx);

    public CodeLabel getTarget(int idx) {
        return targets.get(idx);
    }

}
