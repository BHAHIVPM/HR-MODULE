package com.bhahi.hrmodule.configuration;

import com.bhahi.hrmodule.Utils.VerhoeffUtils;
import com.bhahi.hrmodule.model.auth.UserLogin;
import com.bhahi.hrmodule.repository.auth.UserLoginRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Seeds one default ADMIN account on startup so there's always a way in on a fresh DB.
// Move DEFAULT_USER_ID / DEFAULT_PASSWORD to application.yml or an env var before this
// ever runs against a real environment - hardcoded here just for local/dev bootstrap.
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    // 12-digit login id: [4-digit client][2-digit typeCode][5-digit counter][1-digit Verhoeff]
    //   client = 1000
    //   type   = 12 (ADMIN)
    //   counter= 00001
    //   check  = Verhoeff(client + type + counter)
    private static final String DEFAULT_USER_ID = "10001010001" + VerhoeffUtils.generateCheckDigit("10001010001");
    private static final String DEFAULT_PASSWORD = "Admin@123";

    private final UserLoginRepo userLoginRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userLoginRepo.findByUserId(DEFAULT_USER_ID).isPresent()) {
            return; // already seeded
        }

        UserLogin admin = new UserLogin();
        admin.setUserId(DEFAULT_USER_ID);
        admin.setName("Default Admin");
        admin.setUserMail("admin@example.com");
        admin.setMobileNo("9999999999");
        admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        admin.setUserType(UserLogin.UserType.ADMIN);
        admin.setStatus(UserLogin.UserStatus.ACTIVE);

        userLoginRepo.save(admin); // direct save - bypasses the temp-password flow on purpose
    }
}