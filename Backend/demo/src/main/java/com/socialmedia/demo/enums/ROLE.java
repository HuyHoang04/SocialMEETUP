package com.socialmedia.demo.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public enum ROLE {
    USER,
    ADMIN; // Ví dụ

    // Thêm phương thức này để tương thích với Spring Security
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Thêm tiền tố "ROLE_" theo quy ước của Spring Security
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.name()));
    }
}
