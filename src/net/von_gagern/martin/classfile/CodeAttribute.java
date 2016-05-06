package net.von_gagern.martin.classfile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CodeAttribute extends Attribute implements AttributeOwner {

    public static final String ATTRIBUTE_NAME = "Code";

    int maxStack;

    int maxLocals;

    List<CodeElement> code;

    ExceptionTableEntry[] exceptionTable;

    List<Attribute> attributes;

    public CodeAttribute(ByteBuffer content, AttributeOwner owner) {
        super(ATTRIBUTE_NAME, content, owner);
    }

    public void parse() {
        ClassFile cf = owner.getClassFile();
        CodeListBuilder clb = new CodeListBuilder(cf);
        ByteBuffer content = contentBuffer();
        maxStack = content.getShort() & 0xffff;
        maxLocals = content.getShort() & 0xffff;
        int count = content.getInt();
        int oldLimit = content.limit();
        int codeEnd = content.position() + count;
        content.limit(codeEnd);
        clb.parseCode(content.slice());
        content.limit(oldLimit).position(codeEnd);
        count = content.getShort() & 0xffff;
        exceptionTable = new ExceptionTableEntry[count];
        for (int i = 0; i < count; ++i) {
            ExceptionTableEntry ete = new ExceptionTableEntry(content, cf);
            exceptionTable[i] = ete;
            clb.link(ete);
        }
        try {
            attributes = cf.readAttributes(new BufferDataInput(content), this);
        } catch (IOException e) {
            if (e.getCause() instanceof RuntimeException)
                throw (RuntimeException)e.getCause();
            throw new RuntimeException(e);
        }
        for (Attribute a: attributes) {
            if (a instanceof LineNumberTableAttribute)
                clb.parseLineNumbers((LineNumberTableAttribute)a);
            else if (a instanceof StackMapTableAttribute)
                clb.parseStackMap((StackMapTableAttribute)a);
        }
        code = clb.finish();
    }

    public List<CodeElement> getCode() {
        if (code == null) parse();
        return code;
    }

    public AttributeOwner getParent() {
        return owner;
    }

    public ClassFile getClassFile() {
        return owner.getClassFile();
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public NavigableMap<Integer, Integer> getAddressMap() {
        NavigableMap<Integer, Integer> res = null, combined = null;
        for (Attribute a: attributes) {
            if (a instanceof LineNumberTableAttribute) {
                LineNumberTableAttribute lnt = (LineNumberTableAttribute)a;
                NavigableMap<Integer, Integer> map = lnt.getAddressMap();
                if (res != null) {
                    if (combined == null) {
                        combined = new TreeMap<>(res);
                        res = Collections.unmodifiableNavigableMap(combined);
                    }
                    combined.putAll(map);
                } else {
                    res = map;
                }
            }
        }
        return res;
    }

    @Override public void writeTo(ClassWriter w) {
        if (code == null) parse();
        w.writeU2(maxStack);
        w.writeU2(maxLocals);
        ClassWriter.Deferred count = w.deferU4();
        w.markStartOfCode();
        for (CodeElement op: code)
            op.writeCode(w);
        w.writeU2(exceptionTable.length);
        for (ExceptionTableEntry elt: exceptionTable)
            w.write(elt);
    }

}
