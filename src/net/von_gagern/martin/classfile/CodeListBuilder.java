package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CodeListBuilder {

    ClassFile cf;

    List<CodeElement> lst;

    Map<Integer, CodeLabel> labels;

    CodeLabel start;

    CodeLabel end;

    CodeListBuilder(ClassFile cf) {
        this.cf = cf;
        lst = new ArrayList<>();
        labels = new HashMap<>();
        start = label(0);
    }

    void parseCode(ByteBuffer buf) {
        end = label(buf.remaining());
        while (buf.hasRemaining()) {
            int pos = buf.position();
            OpCode opCode = OpCode.forByte(buf.get());
            Op op = opCode.args.makeOp(opCode, buf, cf);
            op.setAddress(pos, CodeElement.Bias.OP);
            if (op instanceof BranchOp) {
                BranchOp bop = (BranchOp)op;
                CodeLabel target = label(pos + bop.offset);
                bop.target = target;
                target.incoming(bop);
                if (bop instanceof SwitchOp) {
                    SwitchOp sop = (SwitchOp)bop;
                    int[] offsets = sop.offsets;
                    CodeLabel[] targets = new CodeLabel[offsets.length];
                    for (int i = 0; i < offsets.length; ++i) {
                        targets[i] = target = label(pos + offsets[i]);
                        target.incoming(sop);
                    }
                    sop.targets = ClassFile.unmodifiableList(targets);
                }
            }
            lst.add(op);
        }
    }

    private CodeLabel label(int addr) {
        CodeLabel label = labels.get(addr);
        if (label == null) {
            label = new CodeLabel();
            label.setAddress(addr, CodeElement.Bias.LABEL);
            lst.add(label);
            labels.put(addr, label);
        }
        return label;
    }

    void parseLineNumbers(LineNumberTableAttribute lnt) {
        ByteBuffer buf = lnt.contentBuffer();
        int n = buf.getShort() & 0xffff;
        for (int i = 0; i < n; ++i) {
            int startPC = buf.getShort() & 0xffff;
            int lineNumber = buf.getShort() & 0xffff;
            LineNumber ln = new LineNumber(lineNumber);
            ln.setAddress(startPC, CodeElement.Bias.LINENO);
            lst.add(ln);
        }
    }

    void parseStackMap(StackMapTableAttribute smt) {
        int pos = -1;
        for (StackMapFrame frame: smt.getFrames()) {
            pos = pos + 1 + frame.offsetDelta;
            frame.setAddress(pos, CodeElement.Bias.STACKFRAME);
            lst.add(frame);
            for (VerificationTypeInfo itm: frame.getLocalsOfThisFrame())
                link(itm);
            for (VerificationTypeInfo itm: frame.getStackOfThisFrame())
                link(itm);
        }
    }

    private void link(VerificationTypeInfo itm) {
        if (itm.isUninitializedWithOffset())
            itm.offsetLabel = label(itm.getUninitializedOffset());
    }

    void link(ExceptionTableEntry ete) {
        ete.start = label(ete.startAddress);
        ete.end = label(ete.endAddress);
        ete.handler = label(ete.handlerAddress);
    }

    List<CodeElement> finish() {
        lst.sort(new CodeElement.AddressComparator());
        return lst;
    }

}
