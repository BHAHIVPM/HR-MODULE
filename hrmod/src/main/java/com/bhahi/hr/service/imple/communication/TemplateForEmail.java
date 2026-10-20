package com.bhahi.hr.service.imple.communication;

import com.bhahi.hr.service.inter.communication.ITemplateForMessages;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TemplateForEmail implements ITemplateForMessages {

    // Solid background colour for the email body.
    // NOTE: currently a static/solid colour (no logo, no image). Swap this for a
    // background-image later if required.
    private static final String BODY_BACKGROUND_COLOR = "#f2f4f7";

    // Client accent/theme colour. For now it is kept as a static variable and used
    // directly; later it can be passed dynamically per client through the parameter.
    private static final String DEFAULT_THEME_COLOR = "#0d6efd";

    // Text colours (fixed, kept simple).
    private static final String TEXT_COLOR = "#212529";
    private static final String MUTED_TEXT_COLOR = "#6c757d";

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter VALID_TILL_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    // ============================================================================
    // ACCOUNT REGISTRATION - login id + temporary password
    // Simple layout: solid background, no logo, single accent colour used for the
    // header bar and section titles. %s placeholders map to:
    //   1. userName   2. loginId   3. tempPassword
    //   4. themeColor (header/accent)   5. body background   6. text colour
    //   7. muted text colour   8. sent timestamp
    // ============================================================================
    String accountRegistrationHtml = """
            <html>
            <head>
            <style>
            body, p, h1, h2, h3, h4, h5, h6, span, div {
             color:%s !important;
             font-family: Arial, sans-serif;
            }
            .outer_box{
             background-color:%s;
             padding:0;
             margin:0;
            }
            .card{
             background-color:#ffffff;
             padding:32px;
             width:100%%;
             max-width:560px;
             margin:24px auto;
             border:1px solid rgba(0, 0, 0, 0.08);
             border-radius:6px;
            }
            .header{
             background-color:%s;
             padding:20px 32px;
             border-radius:6px 6px 0 0;
            }
            .header h1{
             color:#ffffff !important;
             font-size:18px;
             margin:0;
            }
            .content{
             padding:24px 32px;
            }
            .details-table{
             width:100%%;
             border-collapse:collapse;
             margin:16px 0;
            }
            .details-table td{
             padding:8px 10px;
             border-bottom:1px solid rgba(0,0,0,0.08);
             font-size:14px;
            }
            .details-table td.label{
             font-weight:bold;
             width:40%%;
             color:%s !important;
            }
            .credentials{
             font-weight:bold;
             font-size:15px;
            }
            .note{
             font-size:13px;
             color:%s !important;
            }
            .footer{
             padding:0 32px 24px 32px;
             font-size:12px;
             color:%s !important;
            }
            .divider{
             border:none;
             border-top:1px solid rgba(0, 0, 0, 0.08);
             margin:16px 0;
            }
            </style>
            </head>
            <body class="outer_box">
                <div class="card">
                    <div class="header">
                        <h1>Welcome to HR Module</h1>
                    </div>
                    <div class="content">
                        <p>Dear <strong>%s</strong>,</p>

                        <p>Your account has been created successfully. Please find your login credentials below:</p>

                        <table class="details-table">
                            <tr>
                                <td class="label">Login ID</td>
                                <td class="credentials">%s</td>
                            </tr>
                            <tr>
                                <td class="label">Temporary Password</td>
                                <td class="credentials">%s</td>
                            </tr>
                        </table>

                        <p>For security reasons, please log in and change your temporary password immediately.</p>
                        <p class="note">This is a system-generated email. If you did not expect this, please ignore it or contact the administrator.</p>
                    </div>
                    <hr class="divider">
                    <div class="footer">
                        <p style="margin:4px 0;">For any further clarification, please contact support.</p>
                        <p style="margin:4px 0;">Regards<br><br>Support Team<br>HR Module</p>
                        <p style="margin:4px 0; font-size:11px;">This is a system-generated mail. Please do not reply.</p>
                    </div>
                </div>
                <p style="display:none;">%s</p>
            </body>
            </html>
            """;

    // ============================================================================
    // OTP VERIFICATION
    // Same layout as the registration mail. %s placeholders map to:
    //   1. text colour   2. body background   3. themeColor (header)
    //   4. themeColor (OTP digits)   5. muted text colour (note)   6. muted text colour (footer)
    //   7. userName   8. validity minutes   9. valid-till time   10. otp   11. sent timestamp
    // ============================================================================
    String otpHtml = """
            <html>
            <head>
            <style>
            body, p, h1, h2, h3, h4, h5, h6, span, div {
             color:%s !important;
             font-family: Arial, sans-serif;
            }
            .outer_box{
             background-color:%s;
             padding:0;
             margin:0;
            }
            .card{
             background-color:#ffffff;
             padding:32px;
             width:100%%;
             max-width:560px;
             margin:24px auto;
             border:1px solid rgba(0, 0, 0, 0.08);
             border-radius:6px;
            }
            .header{
             background-color:%s;
             padding:20px 32px;
             border-radius:6px 6px 0 0;
            }
            .header h1{
             color:#ffffff !important;
             font-size:18px;
             margin:0;
            }
            .content{
             padding:24px 32px;
            }
            .otp{
             color:%s !important;
             font-size:28px;
             font-weight:bold;
             letter-spacing:8px;
             text-align:center;
             margin:20px 0;
             padding:14px;
             background-color:#f2f4f7;
             border-radius:6px;
            }
            .note{
             font-size:13px;
             color:%s !important;
            }
            .footer{
             padding:0 32px 24px 32px;
             font-size:12px;
             color:%s !important;
            }
            .divider{
             border:none;
             border-top:1px solid rgba(0, 0, 0, 0.08);
             margin:16px 0;
            }
            </style>
            </head>
            <body class="outer_box">
                <div class="card">
                    <div class="header">
                        <h1>OTP Verification</h1>
                    </div>
                    <div class="content">
                        <p>Dear <strong>%s</strong>,</p>

                        <p>Use the following one-time password (OTP) to verify your identity. This OTP is valid for %s minutes, till %s.</p>

                        <p class="otp">%s</p>

                        <p>Please do not share this OTP with anyone.</p>
                        <p class="note">This is a system-generated email. If you did not initiate this request, please ignore it or contact the administrator.</p>
                    </div>
                    <hr class="divider">
                    <div class="footer">
                        <p style="margin:4px 0;">For any further clarification, please contact support.</p>
                        <p style="margin:4px 0;">Regards<br><br>Support Team<br>HR Module</p>
                        <p style="margin:4px 0; font-size:11px;">This is a system-generated mail. Please do not reply.</p>
                    </div>
                </div>
                <p style="display:none;">%s</p>
            </body>
            </html>
            """;

    @Override
    public String accountRegistration(String userName, String loginId, String tempPassword, String themeColor) {
        // Fall back to the static theme colour if no client colour is supplied.
        String accentColor = (themeColor == null || themeColor.isBlank()) ? DEFAULT_THEME_COLOR : themeColor;

        return accountRegistrationHtml.formatted(
                TEXT_COLOR,
                BODY_BACKGROUND_COLOR,
                accentColor,
                MUTED_TEXT_COLOR,
                MUTED_TEXT_COLOR,
                MUTED_TEXT_COLOR,
                userName,
                loginId,
                tempPassword,
                System.currentTimeMillis()
        );
    }

    @Override
    public String otpMail(String userName, String otp, int validMinutes, String themeColor) {
        String accentColor = (themeColor == null || themeColor.isBlank()) ? DEFAULT_THEME_COLOR : themeColor;
        String displayName = (userName == null || userName.isBlank()) ? "Sir/Madam" : escapeHtml(userName.trim());
        String validTill = ZonedDateTime.now(IST).plusMinutes(validMinutes).format(VALID_TILL_FORMAT) + " IST";

        return otpHtml.formatted(
                TEXT_COLOR,
                BODY_BACKGROUND_COLOR,
                accentColor,
                accentColor,
                MUTED_TEXT_COLOR,
                MUTED_TEXT_COLOR,
                displayName,
                String.valueOf(validMinutes),
                validTill,
                otp,
                System.currentTimeMillis()
        );
    }

    // The user name comes from the database, so escape it before putting it in HTML.
    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}