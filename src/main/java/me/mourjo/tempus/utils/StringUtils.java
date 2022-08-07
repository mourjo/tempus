package me.mourjo.tempus.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Pattern;

public class StringUtils {
    public static String cleanse(String s) {
        StringBuilder sb = new StringBuilder();
        Character last = null;

        for (char c : s.toCharArray()) {
            if (last == null) {
                if (c != ' ') {
                    sb.append(Character.toLowerCase(c));
                }
            } else if (c >= 'a' && c <= 'z') {
                sb.append(c);
            } else if (c >= 'A' && c <= 'Z') {
                sb.append(Character.toLowerCase(c));
            } else if (c == ' ') {
                if (last != c) {
                    sb.append(c);
                }
            }

            last = c;
        }

        if (sb.length() > 0 && sb.lastIndexOf(" ") == sb.length() - 1) {
            sb.replace(sb.length() - 1, sb.length(), "");
        }

        return sb.toString();
    }

    public static Type gsonStringTypeToken() {
        return new TypeToken<Map<String, String>>() {
        }.getType();
    }

    public static Pattern globToRegex(String glob) {
        // taken from https://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
        StringBuilder out = new StringBuilder("^");
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '*':
                    out.append(".*");
                    break;
                case '?':
                    out.append('.');
                    break;
                case '.':
                    out.append("\\.");
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                default:
                    out.append(c);
            }
        }
        out.append('$');
        return Pattern.compile(out.toString());
    }
}
