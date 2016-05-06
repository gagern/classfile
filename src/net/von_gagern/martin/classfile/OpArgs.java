package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;

public enum OpArgs {

    NONE(0) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new NoArgsOp(code);
        }
    },

    CP1(1) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new ConstantOp(code, buf.get() & 0xff, cf);
        }
    },

    CP2(2) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new ConstantOp(code, buf.getShort() & 0xffff, cf);
        }
    },

    LV1(1) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new LocalVarOp(code, buf.get() & 0xff);
        }
    },

    I8(1) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new ImmediateOp(code, buf.get() & 0xff);
        }
    },

    I16(1) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new ImmediateOp(code, buf.getShort() & 0xffff);
        }
    },

    OFF2(2) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new BranchOp(code, buf.position() - 1, buf.getShort());
        }
    },

    OFF4(4) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new BranchOp(code, buf.position() - 1, buf.getShort());
        }
    },

    NEWARRAY(1) {
        final Class<?>[] TABLE = {
            boolean.class,
            char.class,
            float.class,
            double.class,
            byte.class,
            short.class,
            int.class,
            long.class
        };
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            byte b = buf.get();
            Class<?> t = TABLE[b - 4];
            return new NewArrayOp(t);
        }
    },

    IINC(2) { // LV, i8
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new IIncOp(buf.get() & 0xff, buf.get());
        }
    },

    TABLESWITCH(-1) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new TableSwitchOp(buf);
        }
        @Override public int getNumBytes(ByteBuffer buf, int idx) {
            int i = (idx + 3) & (~3);
            int low = buf.getInt(i + 4);
            int high = buf.getInt(i + 8);
            return i - idx + ((high - low + 4) << 2);
        }
    },

    LOOKUPSWITCH(-1) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            return new LookupSwitchOp(buf);
        }
        @Override public int getNumBytes(ByteBuffer buf, int idx) {
            int i = (idx + 3) & (~3);
            int npairs = buf.getInt(i + 4);
            return i - idx + 8 + (npairs << 3);
        }
    },

    INVOKEDYNAMIC(4) { // CP, 00
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            Op op = new ConstantOp(code, buf.getShort() & 0xffff, cf);
            if (buf.getShort() != 0)
                throw new IllegalArgumentException("Misformed instruction");
            return op;
        }
    },

    WIDE(-1) {
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            code = OpCode.forByte(buf.get());
            LocalVarOp op;
            if (code.args == LV1) {
                op = new LocalVarOp(code, buf.getShort() & 0xffff);
            } else if (code.args == IINC) {
                op = new IIncOp(buf.getShort() & 0xffff, buf.getShort());
            } else {
                throw new IllegalArgumentException
                    ("Not a valid wide instruction");
            }
            op.wide = true;
            return op;
        }
        @Override public int getNumBytes(ByteBuffer buf, int idx) {
            OpCode op = OpCode.forByte(buf.get(idx));
            if (op.args == LV1) return 3;
            if (op.args == IINC) return 5;
            throw new IllegalArgumentException("Not a valid wide instruction");
        }
    },

    MULTIANEWARRAY(3) { // CP, u8
        public Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf) {
            int type = buf.getShort() & 0xffff;
            int dim = buf.get() & 0xff;
            return new MultiANewArrayOp(type, dim, cf);
        }
    };

    private final int numBytes;

    OpArgs(int numBytes) {
        this.numBytes = numBytes;
    }

    public int getNumBytes(ByteBuffer buf, int idx) {
        return numBytes;
    }

    public abstract Op makeOp(OpCode code, ByteBuffer buf, ClassFile cf);

}
