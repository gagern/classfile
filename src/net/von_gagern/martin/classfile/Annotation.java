package net.von_gagern.martin.classfile;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

class Annotation implements ClassWriter.Writable {

    Constant.Utf8 type;
    Map<Constant.Utf8, AnnotationValue> elements;

    Annotation(ByteBuffer buf, ClassFile cf) {
        type = (Constant.Utf8)cf.getConstant(buf.getShort() & 0xffff);
        elements = new LinkedHashMap<>();
        int n = buf.getShort() & 0xffff;
        for (int i = 0; i < n; ++i) {
            Constant name = cf.getConstant(buf.getShort() & 0xffff);
            AnnotationValue val = readValue(buf, cf);
            elements.put((Constant.Utf8)name, val);
        }
        elements = Collections.unmodifiableMap(elements);
    }

    public void writeTo(ClassWriter w) {
        w.write2(type);
        w.writeU2(elements.size());
        for (Map.Entry<Constant.Utf8, AnnotationValue> elt:
                 elements.entrySet()) {
            w.write2(elt.getKey());
            w.write(elt.getValue());
        }
    }

    private static AnnotationValue readValue(ByteBuffer buf, ClassFile cf) {
        char tag = (char)(buf.get() & 0xff);
        int word = buf.getShort() & 0xffff;
        switch (tag) {
        case 'B':
            return new AnnotationValue.Const
                (Descriptor.BYTE, (Constant.Integer)cf.getConstant(word));
        case 'C':
            return new AnnotationValue.Const
                (Descriptor.CHAR, (Constant.Integer)cf.getConstant(word));
        case 'D':
            return new AnnotationValue.Const
                (Descriptor.DOUBLE, (Constant.Double)cf.getConstant(word));
        case 'F':
            return new AnnotationValue.Const
                (Descriptor.FLOAT, (Constant.Float)cf.getConstant(word));
        case 'I':
            return new AnnotationValue.Const
                (Descriptor.INT, (Constant.Integer)cf.getConstant(word));
        case 'J':
            return new AnnotationValue.Const
                (Descriptor.LONG, (Constant.Long)cf.getConstant(word));
        case 'S':
            return new AnnotationValue.Const
                (Descriptor.SHORT, (Constant.Integer)cf.getConstant(word));
        case 'Z':
            return new AnnotationValue.Const
                (Descriptor.BOOLEAN, (Constant.Integer)cf.getConstant(word));
        case 's':
            return new AnnotationValue.String
                ((Constant.Utf8)cf.getConstant(word));
        case 'e':
            int word2 = buf.getShort() & 0xffff;
            return new AnnotationValue.Enum
                ((Constant.Utf8)cf.getConstant(word),
                 (Constant.Utf8)cf.getConstant(word2));
        case 'c':
            return new AnnotationValue.Class
                ((Constant.Utf8)cf.getConstant(word));
        case '@':
            buf.position(buf.position() - 2); // un-read word
            return new AnnotationValue.NestedAnnotation
                (new Annotation(buf, cf));
        case '[':
            AnnotationValue[] elts = new AnnotationValue[word];
            for (int i = 0; i < word; ++i)
                elts[i] = readValue(buf, cf);
            return new AnnotationValue.Array(elts);
        default:
            throw new IllegalArgumentException
                ("Invalid annotation value tag: " + tag);
        }
    }

}
