package net.von_gagern.martin.classfile;

import java.util.ArrayList;
import java.util.Collection;

public class CodeLabel extends NonOpElement {

    Collection<BranchOp> incomingBranches;

    public CodeLabel() {}

    void incoming(BranchOp op) {
        if (incomingBranches == null)
            incomingBranches = new ArrayList<>();
        if (!incomingBranches.contains(op))
            incomingBranches.add(op);
    }

}
