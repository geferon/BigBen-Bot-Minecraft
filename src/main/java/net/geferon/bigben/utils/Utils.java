package net.geferon.bigben.utils;

import java.util.Set;

public class Utils {

    /**
     * Try to parse a string
     *
     * @param s The string to be parsed as an integer
     * @param result The parsed Integer
     * @return Whether the string could be parsed as an integer or not
     */
    public static boolean TryParseInteger(String s, InOutParam<Integer> result) {
        if (s == null || result == null) {
            return false;
        }

        try {
            result.setValue(Integer.parseInt(s));

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Do the set contain any of the elements
     *
     * @param <T> The type of the elements in the set
     * @param keySet The set to check
     * @param values The values that we want to check
     * @return Whether any of the values are contained in the set or not
     */
    public static <T> boolean containsAny(Set<T> keySet, T[] values) {
        if (keySet == null || values == null) {
            return false;
        }

        for (T entry : values) {
            if (keySet.contains(entry)) {
                return true;
            }
        }
        return false;
    }
}