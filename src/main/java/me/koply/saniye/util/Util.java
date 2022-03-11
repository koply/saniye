package me.koply.saniye.util;

public class Util {

    public static Integer parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ex) {
            return null;
        }
    }

}