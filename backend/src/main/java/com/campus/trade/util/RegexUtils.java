package com.campus.trade.util;

public class RegexUtils {
    public static final String STUDENT_ID_PATTERN = "^\\d{14}$";

    public static boolean isValidStudentId(String studentId) {
        if (studentId == null) return false;
        return studentId.matches(STUDENT_ID_PATTERN);
    }
}