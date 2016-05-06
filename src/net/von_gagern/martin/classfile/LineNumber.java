package net.von_gagern.martin.classfile;

public class LineNumber extends NonOpElement {

    int line;

    public LineNumber(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

}
