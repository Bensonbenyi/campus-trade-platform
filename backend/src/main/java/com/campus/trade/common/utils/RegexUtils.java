package com.campus.trade.common.utils;

import java.util.regex.Pattern;

public final class RegexUtils {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^20\\d{8}$");

    private RegexUtils() {
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId).matches();
    }
}
