package com.example.ServerSpring.utils;

import org.apache.commons.lang3.StringUtils;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public class TransliterationUtils {

    private static final Map<Character, String> TRANSLITERATION_MAP = new HashMap<>();

    static {
        TRANSLITERATION_MAP.put('А', "A");
        TRANSLITERATION_MAP.put('Б', "B");
        TRANSLITERATION_MAP.put('В', "V");
        TRANSLITERATION_MAP.put('Г', "G");
        TRANSLITERATION_MAP.put('Д', "D");
        TRANSLITERATION_MAP.put('Е', "E");
        TRANSLITERATION_MAP.put('Ё', "E");
        TRANSLITERATION_MAP.put('Ж', "Zh");
        TRANSLITERATION_MAP.put('З', "Z");
        TRANSLITERATION_MAP.put('И', "I");
        TRANSLITERATION_MAP.put('Й', "Y");
        TRANSLITERATION_MAP.put('К', "K");
        TRANSLITERATION_MAP.put('Л', "L");
        TRANSLITERATION_MAP.put('М', "M");
        TRANSLITERATION_MAP.put('Н', "N");
        TRANSLITERATION_MAP.put('О', "O");
        TRANSLITERATION_MAP.put('П', "P");
        TRANSLITERATION_MAP.put('Р', "R");
        TRANSLITERATION_MAP.put('С', "S");
        TRANSLITERATION_MAP.put('Т', "T");
        TRANSLITERATION_MAP.put('У', "U");
        TRANSLITERATION_MAP.put('Ф', "F");
        TRANSLITERATION_MAP.put('Х', "Kh");
        TRANSLITERATION_MAP.put('Ц', "Ts");
        TRANSLITERATION_MAP.put('Ч', "Ch");
        TRANSLITERATION_MAP.put('Ш', "Sh");
        TRANSLITERATION_MAP.put('Щ', "Shch");
        TRANSLITERATION_MAP.put('Ы', "Y");
        TRANSLITERATION_MAP.put('Э', "E");
        TRANSLITERATION_MAP.put('Ю', "Yu");
        TRANSLITERATION_MAP.put('Я', "Ya");
        TRANSLITERATION_MAP.put('а', "a");
        TRANSLITERATION_MAP.put('б', "b");
        TRANSLITERATION_MAP.put('в', "v");
        TRANSLITERATION_MAP.put('г', "g");
        TRANSLITERATION_MAP.put('д', "d");
        TRANSLITERATION_MAP.put('е', "e");
        TRANSLITERATION_MAP.put('ё', "e");
        TRANSLITERATION_MAP.put('ж', "zh");
        TRANSLITERATION_MAP.put('з', "z");
        TRANSLITERATION_MAP.put('и', "i");
        TRANSLITERATION_MAP.put('й', "y");
        TRANSLITERATION_MAP.put('к', "k");
        TRANSLITERATION_MAP.put('л', "l");
        TRANSLITERATION_MAP.put('м', "m");
        TRANSLITERATION_MAP.put('н', "n");
        TRANSLITERATION_MAP.put('о', "o");
        TRANSLITERATION_MAP.put('п', "p");
        TRANSLITERATION_MAP.put('р', "r");
        TRANSLITERATION_MAP.put('с', "s");
        TRANSLITERATION_MAP.put('т', "t");
        TRANSLITERATION_MAP.put('у', "u");
        TRANSLITERATION_MAP.put('ф', "f");
        TRANSLITERATION_MAP.put('х', "kh");
        TRANSLITERATION_MAP.put('ц', "ts");
        TRANSLITERATION_MAP.put('ч', "ch");
        TRANSLITERATION_MAP.put('ш', "sh");
        TRANSLITERATION_MAP.put('щ', "shch");
        TRANSLITERATION_MAP.put('ы', "y");
        TRANSLITERATION_MAP.put('э', "e");
        TRANSLITERATION_MAP.put('ю', "yu");
        TRANSLITERATION_MAP.put('я', "ya");
    }

    public static String transliterate(String text) {
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            String transliteratedChar = TRANSLITERATION_MAP.get(c);
            builder.append(transliteratedChar != null ? transliteratedChar : c);
        }
        return builder.toString();
    }
}
