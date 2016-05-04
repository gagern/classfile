package net.von_gagern.martin.classfile;

import java.util.Iterator;
import java.util.List;

public interface AnnotationValue extends ClassWriter.Writable {

    public static class Const implements AnnotationValue {

        Descriptor.Atomic descriptor;
        Constant value;

        Const(Descriptor.Atomic descriptor, Constant value) {
            this.descriptor = descriptor;
            this.value = value;
        }

        @Override public void writeTo(ClassWriter w) {
            w.writeU1(descriptor.code);
            w.write2(value);
        }

        public java.lang.String toString() {
            return value.toString();
        }

    }

    public static class String implements AnnotationValue {

        Constant.Utf8 value;

        String(Constant.Utf8 value) {
            this.value = value;
        }

        @Override public void writeTo(ClassWriter w) {
            w.writeU1('s');
            w.write2(value);
        }

        public java.lang.String toString() {
            return Util.quote(value.toString()).toString();
        }

    }

    public static class Enum implements AnnotationValue {

        Constant.Utf8 type;
        Constant.Utf8 name;

        Enum(Constant.Utf8 type, Constant.Utf8 name) {
            this.type = type;
            this.name = name;
        }

        @Override public void writeTo(ClassWriter w) {
            w.writeU1('e');
            w.write2(type);
            w.write2(name);
        }

        public java.lang.String toString() {
            return type.toString().replace('/', '.') +
                "." + name.toString();
        }

    }

    public static class Class implements AnnotationValue {

        Constant.Utf8 name;

        Class(Constant.Utf8 name) {
            this.name = name;
        }

        @Override public void writeTo(ClassWriter w) {
            w.writeU1('c');
            w.write2(name);
        }

        public java.lang.String toString() {
            return name.toString().replace('/', '.') + ".class";
        }

    }

    public static class NestedAnnotation implements AnnotationValue {

        Annotation annotation;

        NestedAnnotation(Annotation annotation) {
            this.annotation = annotation;
        }

        @Override public void writeTo(ClassWriter w) {
            w.writeU1('@');
            w.write(annotation);
        }

        public java.lang.String toString() {
            return annotation.toString();
        }

    }
    
    public static class Array
        implements AnnotationValue, Iterable<AnnotationValue>
    {

        List<AnnotationValue> elements;

        Array(AnnotationValue[] elements) {
            this.elements = ClassFile.unmodifiableList(elements);
        }

        public List<AnnotationValue> getElements() {
            return elements;
        }

        public Iterator<AnnotationValue> iterator() {
            return elements.iterator();
        }

        @Override public void writeTo(ClassWriter w) {
            w.writeU1('[');
            w.writeU2(elements.size());
            for (AnnotationValue elt: elements)
                w.write(elt);
        }

        public java.lang.String toString() {
            StringBuilder buf = new StringBuilder().append('{');
            java.lang.String sep = "";
            for (AnnotationValue elt: elements) {
                buf.append(sep).append(elt);
                sep = ", ";
            }
            return buf.append('}').toString();
        }

    }

}
