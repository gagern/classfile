package net.von_gagern.martin.classfile;

import java.util.Locale;

public class Util {

    private Util() {}

    public static CharSequence quote(CharSequence str) {
        StringBuilder buf = new StringBuilder(str.length() + 2);
        buf.append('"');
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            switch(c) {
            case '\\':
                buf.append('\\').append(c);
                break;
            case '\n': buf.append("\\n"); break;
            case '\r': buf.append("\\r"); break;
            case '\t': buf.append("\\t"); break;
            case '\f': buf.append("\\f"); break;
            case '\b': buf.append("\\b"); break;
            default:
                if (c >= 0x20 && c < 0x7f) {
                    buf.append(c);
                } else {
                    buf.append(String.format((Locale)null, "\\u%04x", (int)c));
                }
            }
        }
        buf.append('"');
        return buf;
    }

}
