package com.transgen.test;

public class EstimateRows {
    private static final int SUBMODE_ALPHA = 0;
    private static final int SUBMODE_LOWER = 1;
    private static final int SUBMODE_MIXED = 2;
    private static final int SUBMODE_PUNCTUATION = 3;

    public static int estimate(String msg, int columns) {
        int sourceCodeWords = calculateSourceCodeWords(msg);
        int errorCorrectionCodeWords = getErrorCorrectionCodewordCount(0);
        return calculateNumberOfRows(sourceCodeWords, errorCorrectionCodeWords, columns);
    }

    public static int calculateNumberOfRows(int sourceCodeWords, int errorCorrectionCodeWords, int columns) {
        int rows = ((sourceCodeWords + 1 + errorCorrectionCodeWords) / columns) + 1;
        if (columns * rows >= (sourceCodeWords + 1 + errorCorrectionCodeWords + columns)) {
            rows--;
        }
        return rows;
    }

    public static int getErrorCorrectionCodewordCount(int errorCorrectionLevel) {
        if (errorCorrectionLevel < 0 || errorCorrectionLevel > 8) {
            throw new IllegalArgumentException("Error correction level must be between 0 and 8!");
        }
        return 1 << (errorCorrectionLevel + 1);
    }

    private static boolean isAlphaUpper(char ch) {
        return ch == ' ' || (ch >= 'A' && ch <= 'Z');
    }

    private static boolean isAlphaLower(char ch) {
        return ch == ' ' || (ch >= 'a' && ch <= 'z');
    }

    private static boolean isMixed(char ch) {
        return "\t\r #$%&*+,-./0123456789:=^".indexOf(ch) > -1;
    }

    private static boolean isPunctuation(char ch) {
        return "\t\n\r!\"$'()*,-./:;<>?@[\\]_`{|}~".indexOf(ch) > -1;
    }

    public static int calculateSourceCodeWords(String msg) {
        int len = 0;
        int submode = SUBMODE_ALPHA;
        int msgLength = msg.length();
        for (int idx = 0; idx < msgLength; ) {
            char ch = msg.charAt(idx);
            switch (submode) {
                case SUBMODE_ALPHA:
                    if (isAlphaUpper(ch)) {
                        len++;
                    } else {
                        if (isAlphaLower(ch)) {
                            submode = SUBMODE_LOWER;
                            len++;
                            continue;
                        } else if (isMixed(ch)) {
                            submode = SUBMODE_MIXED;
                            len++;
                            continue;
                        } else {
                            len += 2;
                            break;
                        }
                    }
                    break;
                case SUBMODE_LOWER:
                    if (isAlphaLower(ch)) {
                        len++;
                    } else {
                        if (isAlphaUpper(ch)) {
                            len += 2;
                            break;
                        } else if (isMixed(ch)) {
                            submode = SUBMODE_MIXED;
                            len++;
                            continue;
                        } else {
                            len += 2;
                            break;
                        }
                    }
                    break;
                case SUBMODE_MIXED:
                    if (isMixed(ch)) {
                        len++;
                    } else {
                        if (isAlphaUpper(ch)) {
                            submode = SUBMODE_ALPHA;
                            len++;
                            continue;
                        } else if (isAlphaLower(ch)) {
                            submode = SUBMODE_LOWER;
                            len++;
                            continue;
                        } else {
                            if (idx + 1 < msgLength) {
                                char next = msg.charAt(idx + 1);
                                if (isPunctuation(next)) {
                                    submode = SUBMODE_PUNCTUATION;
                                    len++;
                                    continue;
                                }
                            }
                            len += 2;
                        }
                    }
                    break;
                default:
                    if (isPunctuation(ch)) {
                        len++;
                    } else {
                        submode = SUBMODE_ALPHA;
                        len++;
                        continue;
                    }
                    break;
            }
            idx++; // Don't increment if 'continue' was used.
        }
        return (len + 1) / 2;
    }
}