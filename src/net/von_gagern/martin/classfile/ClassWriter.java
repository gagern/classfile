package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassWriter {

    private ByteBuffer buf;

    private List<Constant> constantPool;

    public ClassWriter() {
        this(1024 * 16);
    }

    public ClassWriter(int initialCapacity) {
        if (initialCapacity < 16) initialCapacity = 16;
        buf = ByteBuffer.allocate(initialCapacity);
        constantPool = new ArrayList<>();
        constantPool.add(null);
    }

    private void ensureSpace(int numBytes) {
        if (buf.remaining() < numBytes) {
            buf.flip();
            ByteBuffer nbuf = ByteBuffer.allocate(buf.capacity() * 4);
            nbuf.put(buf);
            buf = nbuf;
        }
    }

    private void ensureRange(int v, int lo, int hi) {
        if (v < lo || v > hi)
            throw new IllegalArgumentException
                ("Argument " + v + " out of range " + lo + " - " + hi);
    }

    private void ensureRange(long v, long lo, long hi) {
        if (v < lo || v > hi)
            throw new IllegalArgumentException
                ("Argument " + v + " out of range " + lo + " - " + hi);
    }

    public void writeU1(int v) {
        ensureSpace(1);
        ensureRange(v, 0, 0xff);
        buf.put((byte)v);
    }

    public void writeI1(int v) {
        ensureSpace(1);
        ensureRange(v, Byte.MIN_VALUE, Byte.MAX_VALUE);
        buf.put((byte)v);
    }

    public void writeU2(int v) {
        ensureSpace(2);
        ensureRange(v, 0, 0xffff);
        buf.putShort((short)v);
    }

    public void writeI2(int v) {
        ensureSpace(2);
        ensureRange(v, Short.MIN_VALUE, Short.MAX_VALUE);
        buf.putShort((short)v);
    }

    public void writeU4(int v) {
        ensureSpace(4);
        ensureRange(v, 0, 0x7fffffff);
        buf.putInt(v);
    }

    public void writeU4(long v) {
        ensureSpace(4);
        ensureRange(v, 0L, 0xffffffffL);
        buf.putInt((int)v);
    }

    public void writeI4(int v) {
        ensureSpace(4);
        buf.putInt(v);
    }

    public void writeI8(long v) {
        ensureSpace(8);
        buf.putLong(v);
    }

    public void write(float v) {
        ensureSpace(4);
        buf.putFloat(v);
    }

    public void write(double v) {
        ensureSpace(8);
        buf.putDouble(v);
    }

    public void write1(Constant c) {
        writeU1(cpIndex(c));
    }

    public void write2(Constant c) {
        writeU2(cpIndex(c));
    }

    public int cpIndex(Constant c) {
        int idx = constantPool.indexOf(c);
        if (idx >= 0) return idx;
        idx = constantPool.size();
        constantPool.add(c);
        if (c.doubleSize())
            constantPool.add(Constant.INVALID);
        return idx;
    }

    public void write(ByteBuffer data) {
        ensureSpace(data.remaining());
        buf.put(data);
    }

    public void write(ClassWriter.Writable w) {
        w.writeTo(this);
    }

    public interface Writable {
        void writeTo(ClassWriter w);
    }

    public Deferred deferU1() {
        return new Deferred(1);
    }

    public Deferred deferU2() {
        return new Deferred(2);
    }

    public Deferred deferU4() {
        return new Deferred(4);
    }

    public class Deferred {

        private int pos;

        private int length;

        int base;

        Deferred(int length) {
            this.length = length;
            pos = buf.position();
            ensureSpace(length);
            buf.position(pos + length); // leave space
        }

        public void write(int v) {
            switch(length) {
            case 1:
                ensureRange(v, 0, 0xff);
                buf.put(pos, (byte)v);
                break;
            case 2:
                ensureRange(v, 0, 0xffff);
                buf.putShort(pos, (short)v);
                break;
            case 4:
                ensureRange(v, 0, 0x7fffffff);
                buf.putInt(pos, v);
                break;
            default:
                throw new IllegalArgumentException("Invalid length");
            }
        }

        void link(int targetAddr) {
            write(targetAddr - base);
        }

        public void writeByteCount() {
            write(buf.position() - pos - length);
        }

    }

    private int startOfCode;

    public void markStartOfCode() {
        startOfCode = buf.position();
        knownLabels.clear();
        unknownLabels.clear();
    }

    public int posInCode() {
        return buf.position() - startOfCode;
    }

    public int align4() {
        int a = buf.position() - startOfCode;
        int b = (a + 3) & (~3);
        int d = b - a;
        ensureSpace(d);
        for (int i = 0; i < d; ++i)
            buf.put((byte)0);
        return d;
    }

    private Map<CodeLabel, Integer> knownLabels = new HashMap<>();

    private Map<CodeLabel, List<Deferred>> unknownLabels = new HashMap<>();

    public void linkTarget(CodeLabel lbl) {
        int addr = buf.position() - startOfCode;
        knownLabels.put(lbl, addr);
        List<Deferred> lst = unknownLabels.remove(lbl);
        if (lst != null)
            for (Deferred lnk: lst)
                lnk.link(addr);
    }

    public void linkOffset2(CodeLabel target, int base) {
        linkOffset(target, base, 2);
    }

    public void linkOffset4(CodeLabel target, int base) {
        linkOffset(target, base, 4);
    }

    private void linkOffset(CodeLabel target, int base, int length) {
        Integer addr = knownLabels.get(target);
        if (addr != null) {
            if (length == 4)
                writeI4(addr - base);
            else
                writeI2(addr - base);
            return;
        }
        Deferred d = new Deferred(length);
        d.base = base;
        List<Deferred> lst = unknownLabels.get(target);
        if (lst == null) {
            lst = new ArrayList<>(1);
            unknownLabels.put(target, lst);
        }
        lst.add(d);
    }

}
