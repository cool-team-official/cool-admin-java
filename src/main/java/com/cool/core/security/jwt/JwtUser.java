package com.cool.core.security.jwt;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 用户信息
 */
@Data
public class JwtUser implements UserDetails {

    public JwtUser(String username, String password, List<GrantedAuthority> perms, Boolean status) {
        this.username = username;
        this.password = password;
        this.perms = perms;
        this.status = status;
    }

    private String username;
    private String password;
    private Boolean status;
    private List<GrantedAuthority> perms;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return perms;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status;
    }
}
