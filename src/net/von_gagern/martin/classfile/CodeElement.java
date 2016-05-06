package net.von_gagern.martin.classfile;

import java.util.Comparator;

public abstract class CodeElement {

    enum Bias {
        LABEL,
        LINENO,
        STACKFRAME,
        OP
    }

    private int biasedAddress;

    void setAddress(int address, Bias bias) {
        biasedAddress = (address << 2) + bias.ordinal();
    }

    public int getAddress() {
        return biasedAddress >>> 2;
    }

    public abstract void writeCode(ClassWriter w);

    static class AddressComparator implements Comparator<CodeElement> {
        public int compare(CodeElement a, CodeElement b) {
            return a.biasedAddress - b.biasedAddress;
        }
    }

}
