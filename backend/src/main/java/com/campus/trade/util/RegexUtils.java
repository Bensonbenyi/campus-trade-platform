package com.campus.trade.util;

public class RegexUtils {
    public static final String STUDENT_ID_PATTERN = "^20\\d{2}10\\d{4}$";

    public static boolean isValidStudentId(String studentId) {
        if (studentId == null) return false;
        return studentId.matches(STUDENT_ID_PATTERN);
    }
}
