package com.bhahi.hrmodule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
public class UserLogin implements UserDetails {

    @Id
    private int userId;
    @Column(length = 15, nullable = false)
    @NotNull
    private String userCode;
    @NotNull
    @Column( nullable = false)
    private String userName;
    @NotNull    
    @Column(length = 150, nullable = false)
    private String userMail;
    @NotNull
    @Column(length = 10, nullable = false)
    private String mobileNo;
    private String password;
    @NotNull
    private String userType;
    @NotNull
    private enum  UserStatus{Active, Inactive};
    @Column(length = 10, nullable = false)
    private UserStatus status;
    @NotNull
    private String otp;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userType));
    }

    @Override
    public String getUsername() {
        return userCode;
    }


    
    
}
