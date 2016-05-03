package net.von_gagern.martin.classfile;

public interface Op {

    String asmFormat(String indent);

    int getNumBytes();

}
