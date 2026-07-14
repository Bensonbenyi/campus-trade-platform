package com.campus.trade.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegexUtilsTest {

    @Test
    void validatesMainlandMobileNumbers() {
        assertTrue(RegexUtils.isValidPhone("13800138000"));
        assertFalse(RegexUtils.isValidPhone("12800138000"));
        assertFalse(RegexUtils.isValidPhone(null));
    }

    @Test
    void validatesTenDigitStudentIdsStartingWith20() {
        assertTrue(RegexUtils.isValidStudentId("2026101234"));
        assertFalse(RegexUtils.isValidStudentId("202610123"));
        assertFalse(RegexUtils.isValidStudentId("1926101234"));
    }
}
