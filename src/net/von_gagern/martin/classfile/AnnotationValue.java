package net.von_gagern.martin.classfile;

public interface AnnotationValue extends ClassWriter.Writable {

    public static class Const implements AnnotationValue {
        Const(Descriptor.Atomic descriptor, Constant value) {
        }
        @Override public void writeTo(ClassWriter w) {
        }
    }

    public static class String implements AnnotationValue {
        String(Constant.Utf8 value) {
        }
        @Override public void writeTo(ClassWriter w) {
        }
    }

    public static class Enum implements AnnotationValue {
        Enum(Constant.Utf8 type, Constant.Utf8 name) {
        }
        @Override public void writeTo(ClassWriter w) {
        }
    }

    public static class Class implements AnnotationValue {
        Class(Constant.Utf8 name) {
        }
        @Override public void writeTo(ClassWriter w) {
        }
    }

    public static class NestedAnnotation implements AnnotationValue {
        NestedAnnotation(Annotation ann) {
        }
        @Override public void writeTo(ClassWriter w) {
        }
    }
    
    public static class Array implements AnnotationValue {
        Array(AnnotationValue[] elements) {
        }
        @Override public void writeTo(ClassWriter w) {
        }
    }

}
