package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public abstract class StackMapFrame
    extends NonOpElement
    implements ClassWriter.Writable
{

    StackMapFrame previous;

    int offsetDelta;

    StackMapFrame(int offsetDelta) {
        this.offsetDelta = offsetDelta;
    }

    List<VerificationTypeInfo> getLocalsOfThisFrame() {
        return Collections.emptyList();
    }

    List<VerificationTypeInfo> getStackOfThisFrame() {
        return Collections.emptyList();
    }

    static class Same extends StackMapFrame {
        Same(int offsetDelta) {
            super(offsetDelta);
        }
        public void writeTo(ClassWriter w) {
            if (offsetDelta < 64) {
                w.writeU1(offsetDelta);
            } else {
                w.writeU1(251);
                w.writeU2(offsetDelta);
            }
        }
    }

    static class SameLocals1StackItem extends StackMapFrame {
        VerificationTypeInfo stackItem;
        SameLocals1StackItem(int offsetDelta, VerificationTypeInfo stackItem) {
            super(offsetDelta);
            this.stackItem = stackItem;
        }
        public void writeTo(ClassWriter w) {
            if (offsetDelta < 128 - 64) {
                w.writeU1(offsetDelta + 64);
            } else {
                w.writeU1(247);
                w.writeU2(offsetDelta);
            }
            w.write(stackItem);
        }
        List<VerificationTypeInfo> getStackOfThisFrame() {
            return Collections.singletonList(stackItem);
        }
    }

    static class Chop extends StackMapFrame {
        int k;
        Chop(int offsetDelta, int k) {
            super(offsetDelta);
            this.k = k;
        }
        public void writeTo(ClassWriter w) {
            w.writeU1(251 - k);
            w.writeU2(offsetDelta);
        }
    }

    static class Append extends StackMapFrame {
        VerificationTypeInfo[] locals;
        Append(int offsetDelta, VerificationTypeInfo[] locals) {
            super(offsetDelta);
            this.locals = locals;
        }
        public void writeTo(ClassWriter w) {
            w.writeU1(locals.length + 251);
            w.writeU2(offsetDelta);
            for (int i = 0; i < locals.length; ++i)
                w.write(locals[i]);
        }
        List<VerificationTypeInfo> getLocalsOfThisFrame() {
            return ClassFile.unmodifiableList(locals);
        }
    }

    static class Full extends StackMapFrame {
        VerificationTypeInfo[] locals;
        VerificationTypeInfo[] stack;
        Full(int offsetDelta, VerificationTypeInfo[] locals,
             VerificationTypeInfo[] stack) {
            super(offsetDelta);
            this.locals = locals;
            this.stack = stack;
        }
        public void writeTo(ClassWriter w) {
            w.writeU1(255);
            w.writeU2(offsetDelta);
            w.writeU2(locals.length);
            for (int i = 0; i < locals.length; ++i)
                w.write(locals[i]);
            w.writeU2(stack.length);
            for (int i = 0; i < stack.length; ++i)
                w.write(stack[i]);
        }
        List<VerificationTypeInfo> getLocalsOfThisFrame() {
            return ClassFile.unmodifiableList(locals);
        }
        List<VerificationTypeInfo> getStackOfThisFrame() {
            return ClassFile.unmodifiableList(stack);
        }
    }

    static StackMapFrame parse(ByteBuffer buf, ClassFile cf) {
        int type = buf.get() & 0xff;
        if (type < 64) { // same_frame
            return new Same(type);
        } else if (type < 128) { // same_locals_1_stack_item_frame
            VerificationTypeInfo itm = VerificationTypeInfo.parse(buf, cf);
            return new SameLocals1StackItem(type - 64, itm);
        } else if (type < 247) { // reserved
            throw new IllegalArgumentException("Type " + type + " is reserved");
        } else if (type < 248) { // same_locals_1_stack_item_frame_extended
            int offsetDelta = buf.getShort() & 0xffff;
            VerificationTypeInfo itm = VerificationTypeInfo.parse(buf, cf);
            return new SameLocals1StackItem(offsetDelta, itm);
        } else if (type < 251) { // chop_frame
            int offsetDelta = buf.getShort() & 0xffff;
            return new Chop(offsetDelta, 251 - type);
        } else if (type < 252) { // same_frame_extended
            int offsetDelta = buf.getShort() & 0xffff;
            return new Same(offsetDelta);
        } else if (type < 255) { // append_frame
            int offsetDelta = buf.getShort() & 0xffff;
            int n = type - 251;
            VerificationTypeInfo[] locals = new VerificationTypeInfo[n];
            for (int i = 0; i < n; ++i)
                locals[i] = VerificationTypeInfo.parse(buf, cf);
            return new Append(offsetDelta, locals);
        } else { // full_frame
            int offsetDelta = buf.getShort() & 0xffff;
            int n = buf.getShort() & 0xffff;
            VerificationTypeInfo[] locals = new VerificationTypeInfo[n];
            for (int i = 0; i < n; ++i)
                locals[i] = VerificationTypeInfo.parse(buf, cf);
            n = buf.getShort() & 0xffff;
            VerificationTypeInfo[] stack = new VerificationTypeInfo[n];
            for (int i = 0; i < n; ++i)
                stack[i] = VerificationTypeInfo.parse(buf, cf);
            return new Full(offsetDelta, locals, stack);
        }
    }

}
