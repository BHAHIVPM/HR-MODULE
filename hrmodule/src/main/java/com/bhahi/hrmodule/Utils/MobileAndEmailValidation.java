package com.bhahi.hrmodule.Utils;
import com.bhahi.hr.exception.CustomException;

public class MobileAndEmailValidation {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]{2,}$";

    public static void mobileNumValidation(String number) {
        String v = number == null ? null : number.trim();
        boolean bad = (v == null || v.length() != 10);
        if (!bad) {
            for (int i = 0; i < v.length(); i++) {
                char c = v.charAt(i);
                if (c < '0' || c > '9') { bad = true; break; }
            }
        }
        if (bad) {
            throw new CustomException("Invalid mobile number.", "Mobile number must be exactly 10 digits (0-9), please verify it.", 400);
        }
    }

    public static void pinCodeValidation(String number) {
        if (number == null) {
            throw new CustomException("Invalid.", "Invalid pin code number, please verify it.", 400);
        }
        String v = number.trim();
        boolean bad = (v.length() != 6);
        if (!bad) {
            for (int i = 0; i < v.length(); i++) {
                char c = v.charAt(i);
                if (c < '0' || c > '9') { bad = true; break; }
            }
        }
        if (bad) {
            throw new CustomException("Invalid.", "Invalid pin code number, please verify it.", 400);
        }
    }

    public static void emailIdValidation(String email) {
        if (email == null || !email.trim().matches(EMAIL_PATTERN)) {
            throw new CustomException("Invalid Email Format", "The provided email address format is invalid.", 400);
        }
    }
}
