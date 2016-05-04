package net.von_gagern.martin.classfile;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class AccessFlags extends AbstractSet<AccessFlag> {

    public static final Set<AccessFlag> CLASS_FLAGS =
        Collections.unmodifiableSet(EnumSet.of(
            AccessFlag.PUBLIC,
            AccessFlag.FINAL,
            AccessFlag.SUPER,
            AccessFlag.INTERFACE,
            AccessFlag.ABSTRACT,
            AccessFlag.SYNTHETIC,
            AccessFlag.ANNOTATION,
            AccessFlag.ENUM));

    public static final Set<AccessFlag> FIELD_FLAGS =
        Collections.unmodifiableSet(EnumSet.of(
            AccessFlag.PUBLIC,
            AccessFlag.PRIVATE,
            AccessFlag.PROTECTED,
            AccessFlag.STATIC,
            AccessFlag.FINAL,
            AccessFlag.VOLATILE,
            AccessFlag.TRANSIENT,
            AccessFlag.SYNTHETIC,
            AccessFlag.ENUM));

    public static final Set<AccessFlag> METHOD_FLAGS =
        Collections.unmodifiableSet(EnumSet.of(
            AccessFlag.PUBLIC,
            AccessFlag.PRIVATE,
            AccessFlag.PROTECTED,
            AccessFlag.STATIC,
            AccessFlag.FINAL,
            AccessFlag.SYNCHRONIZED,
            AccessFlag.BRIDGE,
            AccessFlag.VARARGS,
            AccessFlag.NATIVE,
            AccessFlag.ABSTRACT,
            AccessFlag.STRICT,
            AccessFlag.SYNTHETIC));

    public static final Set<AccessFlag> INNER_CLASS_FLAGS =
        Collections.unmodifiableSet(EnumSet.of(
            AccessFlag.PUBLIC,
            AccessFlag.PRIVATE,
            AccessFlag.PROTECTED,
            AccessFlag.STATIC,
            AccessFlag.FINAL,
            AccessFlag.INTERFACE,
            AccessFlag.ABSTRACT,
            AccessFlag.SYNTHETIC,
            AccessFlag.ANNOTATION,
            AccessFlag.ENUM));

    public static final Set<AccessFlag> METHOD_PARAMETER_FLAGS =
        Collections.unmodifiableSet(EnumSet.of(
            AccessFlag.FINAL,
            AccessFlag.SYNTHETIC,
            AccessFlag.MANDATED));

    static AccessFlags classFlags(int bits) {
        return new AccessFlags(bits, CLASS_FLAGS);
    }

    static AccessFlags fieldFlags(int bits) {
        return new AccessFlags(bits, FIELD_FLAGS);
    }

    static AccessFlags methodFlags(int bits) {
        return new AccessFlags(bits, METHOD_FLAGS);
    }

    static AccessFlags innerClassFlags(int bits) {
        return new AccessFlags(bits, INNER_CLASS_FLAGS);
    }

    static AccessFlags methodParameterFlags(int bits) {
        return new AccessFlags(bits, METHOD_PARAMETER_FLAGS);
    }

    final int bits;

    final Set<AccessFlag> known;

    AccessFlags(int bits, Set<AccessFlag> known) {
        this.bits = bits;
        this.known = known;
    }

    public int bitPattern() {
        return bits;
    }

    @Override public int size() {
        int n = 0;
        for (AccessFlag flag: known)
            if ((flag.bit & bits) != 0)
                ++n;
        return n;
    }

    @Override public boolean contains(Object obj) {
        return obj instanceof AccessFlag &&
            (bits & ((AccessFlag)obj).bit) != 0 &&
            known.contains(obj);
    }

    public Iterator<AccessFlag> iterator() {
        return new AccessFlagIterator(bits, known.iterator());
    }

    private static class AccessFlagIterator implements Iterator<AccessFlag> {
        private final int bits;
        private final Iterator<AccessFlag> known;
        private AccessFlag next;
        AccessFlagIterator(int bits, Iterator<AccessFlag> known) {
            this.bits = bits;
            this.known = known;
            next = advance();
        }
        private AccessFlag advance() {
            while (known.hasNext()) {
                AccessFlag flag = known.next();
                if ((bits & flag.bit) != 0)
                    return flag;
            }
            return null;
        }
        public boolean hasNext() {
            return next != null;
        }
        public AccessFlag next() {
            AccessFlag next = this.next;
            if (next == null)
                throw new NoSuchElementException();
            this.next = advance();
            return next;
        }
    }

}
