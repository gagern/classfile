package net.von_gagern.martin.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

class BufferDataInput implements DataInput {

    private ByteBuffer buf;

    BufferDataInput(ByteBuffer buf) throws IOException {
        this.buf = buf.order(ByteOrder.BIG_ENDIAN);
    }

    public boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

    public byte readByte() throws IOException {
        try {
            return buf.get();
        } catch (BufferUnderflowException e) {
            throw underflow(e);
        }
    }

    public char readChar() throws IOException {
        try {
            return buf.getChar();
        } catch (BufferUnderflowException e) {
            throw underflow(e);
        }
    }

    public double readDouble() throws IOException {
        try {
            return buf.getDouble();
        } catch (BufferUnderflowException e) {
            throw underflow(e);
        }
    }

    public float readFloat() throws IOException {
        try {
            return buf.getFloat();
        } catch (BufferUnderflowException e) {
            throw underflow(e);
        }
    }

    public void readFully(byte[] dst) throws IOException {
        try {
            buf.get(dst);
        } catch (BufferUnderflowException e) {
            throw underflow(e);
        }
    }

    public void readFully(byte[] dst, int off, int len) throws IOException {
        try {
            buf.get(dst, off, len);
        } catch (BufferUnderflowException e) {
            throw underflow(e);
        }
    }

    public int readInt() throws IOException {
        try {
            return buf.getInt();
        } catch (BufferUnderflowException e) {
            throw underflow(e);
        }
    }

    public String readLine() throws IOException {
        throw new UnsupportedOperationException("No readLine support");
    }

    public long readLong() throws IOException {
        try {
            return buf.getLong();
        } catch (BufferUnderflowException e) {
            throw underflow(e);
        }
    }

    public short readShort() throws IOException {
        try {
            return buf.getShort();
        } catch (BufferUnderflowException e) {
            throw underflow(e);
        }
    }

    public int readUnsignedByte() throws IOException {
        return readByte() & 0xff;
    }

    public int readUnsignedShort() throws IOException {
        return readShort() & 0xffff;
    }

    public String readUTF() throws IOException {
        ByteBuffer bytes = readBuffer(readUnsignedShort());
        return StandardCharsets.UTF_8
            .newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(bytes)
            .toString();
    }

    public int skipBytes(int n) throws IOException {
        if (n < 0)
            return 0;
        if (buf.remaining() < n)
            n = buf.remaining();
        if (n > 0)
            buf.position(buf.position() + n);
        return n;
    }

    private IOException underflow(BufferUnderflowException e) {
        return new IOException(e);
    }

    public ByteBuffer readBuffer(int length) throws IOException {
        if (length > buf.remaining())
            throw new IOException("Buffer underflow");
        int oldLimit = buf.limit();
        int end = buf.position() + length;
        try {
            buf.limit(end);
            return buf.slice();
        } finally {
            buf.limit(oldLimit).position(end);
        }
    }

    public static ByteBuffer readBuffer(DataInput in, int length)
        throws IOException
    {
        if (in instanceof BufferDataInput)
            return ((BufferDataInput)in).readBuffer(length);
        byte[] data = new byte[length];
        in.readFully(data);
        return ByteBuffer.wrap(data);
    }

}
