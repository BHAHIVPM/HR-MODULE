package com.bhahi.hrmodule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user_login")
@Data
public class UserLogin implements UserDetails {
    public enum UserType{ADMIN, EMPLOYEE, USER}

    public enum UserStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tableId;

    // 12 digit login id -> first 4 digits identify the client (tenant).
    @NotNull
    @Column(length = 12, nullable = false, unique = true)
    private String userId;

    @JsonIgnore
    private String tempPassword;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(length = 150, nullable = false)
    private String userMail;

    @NotNull
    @Column(length = 10, nullable = false)
    private String mobileNo;

    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private UserType userType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private UserStatus status;

    @JsonIgnore
    private String otp;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userType.name()));
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
