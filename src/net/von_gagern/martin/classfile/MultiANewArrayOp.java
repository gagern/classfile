package net.von_gagern.martin.classfile;

class MultiANewArrayOp extends ConstantOp {

    int dim;

    public MultiANewArrayOp(int type, int dim) {
        super(OpCode.MULTIANEWARRAY, 4, type);
        this.dim = dim;
    }

    @Override public String formatArgs(String indent) {
        return super.formatArgs(indent) + " " + dim;
    }

}
