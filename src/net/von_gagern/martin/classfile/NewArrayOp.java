package net.von_gagern.martin.classfile;

class NewArrayOp extends OpWithArgs {

    Class<?> type;

    public NewArrayOp(Class<?> type) {
        super(OpCode.NEWARRAY, 2);
        this.type = type;
    }

    public String formatArgs(String indent) {
        return type.toString();
    }

}
