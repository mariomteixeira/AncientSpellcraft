package com.windanesz.ancientspellcraft.util;

/** Transliteracao para elder futhark (1.12.2 LangUtils): troca de caracteres 1:1. */
public final class LangUtils {

    public static String toElderFuthark(String string) {
        return ElderFuthark.getFuthark(string);
    }

    public enum ElderFuthark {

        F("F", "ᚡ"),
        U("U", "ᚢ"),
        V("V", "ᚢ"),
        TH("TH", "ᚦ"),
        A("A", "ᚨ"),
        R("R", "ᚱ"),
        K("K", "ᚲ"),
        C("C", "ᚲ"),
        G("G", "ᚷ"),
        W("W", "ᚹ"),
        H("H", "ᚺ"),
        N("N", "ᚾ"),
        I("I", "ᛁ"),
        J("J", "ᛃ"),
        Y("Y", "ᛃ"),
        AE("AE", "ᛇ"),
        P("P", "ᛈ"),
        Z("Z", "ᛉ"),
        S("S", "ᛊ"),
        T("T", "ᛏ"),
        B("B", "ᛒ"),
        E("E", "ᛖ"),
        M("M", "ᛗ"),
        L("L", "ᛚ"),
        Q("Q", "ᛜ"),
        O("O", "ᛟ"),
        D("D", "ᛞ"),
        COLON(":", "፥");

        private final String latin;
        private final String uniRune;

        ElderFuthark(String latin, String uniRune) {
            this.latin = latin;
            this.uniRune = uniRune;
        }

        private static String getUniRuneForChar(String string) {
            for (ElderFuthark f : ElderFuthark.values()) {
                if (f.latin.equals(string.toUpperCase())) {
                    return f.uniRune;
                }
            }
            return string;
        }

        public static String getFuthark(String string) {
            StringBuilder target = new StringBuilder();
            for (int i = 0; i < string.length(); i++) {
                target.append(getUniRuneForChar(string.substring(i, i + 1)));
            }
            return target.toString();
        }
    }

    private LangUtils() {}
}
