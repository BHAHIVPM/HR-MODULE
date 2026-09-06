package com.bhahi.hrmodule.Utils;

// Verhoeff checksum algorithm — generates and validates a single check digit
// for a numeric string (used here for the 12-digit login id: 11 digits + 1 check digit).
public final class VerhoeffUtils {

    // Multiplication table d[i][j]
    private static final int[][] D = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
            {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
            {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
            {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
            {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
            {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
            {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
            {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
            {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
    };

    // Permutation table p[iteration][index]
    private static final int[][] P = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
            {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
            {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
            {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
            {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
            {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
            {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}
    };

    // Inverse table inv[i]
    private static final int[] INV = {0, 4, 3, 2, 1, 5, 6, 7, 8, 9};

    private VerhoeffUtils() {
    }

    /**
     * Generates the Verhoeff check digit for the given numeric string.
     * The input should NOT already contain a check digit.
     * Returns a single digit (0-9) as a char.
     */
    public static char generateCheckDigit(String number) {
        int c = 0;
        for (int i = 0; i < number.length(); i++) {
            int pos = number.length() - 1 - i;
            int digit = number.charAt(pos) - '0';
            // Generation uses (i+1) % 8 — different from validation's i % 8
            int p = P[(i + 1) % 8][digit];
            c = D[c][p];
        }
        return (char) ('0' + INV[c]);
    }

    /**
     * Validates a full number including its check digit.
     * Returns true if the checksum is valid.
     */
    public static boolean validate(String numberWithCheckDigit) {
        int c = 0;
        for (int i = 0; i < numberWithCheckDigit.length(); i++) {
            int pos = numberWithCheckDigit.length() - 1 - i;
            int digit = numberWithCheckDigit.charAt(pos) - '0';
            c = D[c][P[i % 8][digit]];
        }
        return c == 0;
    }
}
