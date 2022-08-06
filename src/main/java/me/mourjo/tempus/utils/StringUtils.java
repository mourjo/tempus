package me.mourjo.tempus.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

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
}
