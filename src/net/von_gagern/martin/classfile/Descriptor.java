package net.von_gagern.martin.classfile;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.nio.CharBuffer;

public abstract class Descriptor {

    public abstract void vmCode(StringBuilder buf);

    public String toString() {
        StringBuilder buf = new StringBuilder();
        vmCode(buf);
        return buf.toString();
    }

    public static abstract class Value extends Descriptor {
        public abstract String getName();
        public Value getComponentType() {
            return null;
        }
        public Class<?> asClass(ClassLoader cl) throws ClassNotFoundException {
            return Class.forName(getName(), false, cl);
        }
    }

    public static class Atomic extends Value {
        final char code;
        final String name;
        final Class<?> clazz;
        Atomic(char code, String name, Class<?> clazz) {
            this.code = code;
            this.name = name;
            this.clazz = clazz;
        }
        public void vmCode(StringBuilder buf) {
            buf.append(code);
        }
        public String getName() {
            return name;
        }
        @Override public Class<?> asClass(ClassLoader cl) {
            return clazz;
        }
    }

    public static class Array extends Value {
        final Value base;
        Array(Value base) {
            this.base = base;
        }
        @Override public Value getComponentType() {
            return base;
        }
        public void vmCode(StringBuilder buf) {
            buf.append('[');
            base.vmCode(buf);
        }
        public String getName() {
            return toString().replace('/', '.');
        }
    }

    public static class Obj extends Value {
        final CharSequence type;
        Obj(CharSequence type) {
            this.type = type;
        }
        public void vmCode(StringBuilder buf) {
            buf.append('L').append(type).append(';');
        }
        public String getName() {
            return type.toString().replace('/', '.');
        }
    }

    public static class Method extends Descriptor {
        List<Value> args;
        Value ret;
        Method(List<Value> args, Value ret) {
            this.args = args;
            this.ret = ret;
        }
        public void vmCode(StringBuilder buf) {
            buf.append('(');
            for (Value arg: args)
                arg.vmCode(buf);
            buf.append(')');
            ret.vmCode(buf);
        }
        public MethodType asMethodType(ClassLoader cl)
            throws ClassNotFoundException
        {
            Class<?>[] a = new Class<?>[args.size()];
            for (int i = 0; i < args.size(); ++i)
                a[i] = args.get(i).asClass(cl);
            Class<?> r = ret.asClass(cl);
            return MethodType.methodType(r, a);
        }
        public Value getReturnType() {
            return ret;
        }
        public List<Value> getArgumentTypes() {
            return Collections.unmodifiableList(args);
        }
    }

    static final Atomic BYTE = new Atomic('B', "byte", byte.class);
    static final Atomic CHAR = new Atomic('C', "char", char.class);
    static final Atomic DOUBLE = new Atomic('D', "double", double.class);
    static final Atomic FLOAT = new Atomic('F', "float", float.class);
    static final Atomic INT = new Atomic('I', "int", int.class);
    static final Atomic LONG = new Atomic('J', "long", long.class);
    static final Atomic SHORT = new Atomic('S', "short", short.class);
    static final Atomic BOOLEAN = new Atomic('Z', "boolean", boolean.class);
    static final Atomic VOID = new Atomic('V', "void", void.class);
    static final Obj OBJECT = new Obj("java/lang/Object");
    static final Obj STRING = new Obj("java/lang/String");
    static final Obj CLASS = new Obj("java/lang/Class");

    static Descriptor parse(Constant.Utf8 in) {
        String str = in.toString();
        CharBuffer buf = CharBuffer.wrap(str);
        Descriptor res = parse(buf);
        if (buf.hasRemaining())
            throw new IllegalArgumentException
                ("Extraneous descriptor text: " + str);
        return res;
    }

    private static Descriptor parse(CharBuffer in) {
        char c = in.get();
        switch (c) {
            case 'B': return BYTE;
            case 'C': return CHAR;
            case 'D': return DOUBLE;
            case 'F': return FLOAT;
            case 'I': return INT;
            case 'J': return LONG;
            case 'S': return SHORT;
            case 'Z': return BOOLEAN;
            case 'V': return VOID;
            case '[':
                return new Array((Value)parse(in));
            case 'L':
                CharBuffer dup = in.duplicate();
                while (in.get() != ';') {}
                dup.limit(in.position() - 1);
                return new Obj(dup.slice());
            case '(':
                List<Value> args = new ArrayList<>();
                while (in.charAt(0) != ')')
                    args.add((Value)parse(in));
                in.get();
                return new Method(args, (Value)parse(in));
        }
        throw new IllegalArgumentException
            ("Invalid descriptor character: " + c);
    }

}
