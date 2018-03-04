package com.dms.datalayerapi.util;

/**
 * Created by Raja.p on 11-08-2016.
 */
public class Helper {
    public static boolean hasInArray(String[] values, String value) {
        for (String s : values) {
            if (s.equals(value))
                return true;
        }
        return false;
    }
}
