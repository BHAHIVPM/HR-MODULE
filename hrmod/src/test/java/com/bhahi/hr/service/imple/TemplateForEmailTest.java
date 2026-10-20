package com.bhahi.hr.service.imple;

import com.bhahi.hr.service.imple.communication.TemplateForEmail;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateForEmailTest {

    private final TemplateForEmail templateForEmail = new TemplateForEmail();

    @Test
    void accountRegistration_rendersCredentialsAndThemeColor() {
        String html = templateForEmail.accountRegistration(
                "John Doe", "john.doe@company", "Temp@1234", "#0d6efd");

        assertTrue(html.contains("John Doe"), "should contain the user name");
        assertTrue(html.contains("john.doe@company"), "should contain the login id");
        assertTrue(html.contains("Temp@1234"), "should contain the temporary password");
        assertTrue(html.contains("Welcome to HR Module"), "should contain the header");
        assertTrue(html.contains("#0d6efd"), "should apply the provided theme color");
        // solid background, no image/logo
        assertTrue(html.contains("background-color:#f2f4f7"), "should use a solid background colour");
        assertTrue(!html.contains("<img"), "should not contain any logo/image");
        assertTrue(!html.contains("background-image"), "should not use a background image");
        // every %s placeholder must be substituted (no leftover format specifiers)
        assertTrue(!html.contains("%s"), "no format specifier should remain");
    }

    @Test
    void accountRegistration_fallsBackToDefaultThemeColorWhenBlank() {
        String html = templateForEmail.accountRegistration("Jane", "jane", "Pass@1", "  ");
        // blank theme colour -> default static accent colour is applied
        assertTrue(html.contains("#0d6efd"), "should fall back to the default theme colour");
        assertTrue(!html.contains("%s"), "no format specifier should remain");
    }
}
