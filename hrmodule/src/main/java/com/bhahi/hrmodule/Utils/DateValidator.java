package com.bhahi.hrmodule.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public final class DateValidator {

    private DateValidator() {}

    // Not future
    public static boolean notFuture(LocalDate date) {
        return date == null || !date.isAfter(LocalDate.now());
    }

    public static boolean notFuture(LocalDateTime dateTime) {
        return dateTime == null || !dateTime.isAfter(LocalDateTime.now());
    }

    // Not before minimum
    public static boolean notBefore(LocalDate date, LocalDate minDate) {
        return date == null || !date.isBefore(minDate);
    }

    // Between range
    public static boolean between(LocalDate date, LocalDate start, LocalDate end) {
        if (date == null) return true;
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Validate minimum age
     */
    public static boolean minimumAge(LocalDate dob, int age) {
        if (dob == null) return true;
        return Period.between(dob, LocalDate.now()).getYears() >= age;
    }
}
