package net.von_gagern.martin.classfile;

class MultiANewArrayOp extends ConstantOp {

    int dim;

    public MultiANewArrayOp(int type, int dim, ClassFile cf) {
        super(OpCode.MULTIANEWARRAY, 4, type, cf);
        this.dim = dim;
    }

    @Override public String formatArgs(String indent) {
        return super.formatArgs(indent) + " " + dim;
    }

}
