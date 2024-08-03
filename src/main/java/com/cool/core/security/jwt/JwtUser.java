package com.cool.core.security.jwt;

import com.cool.core.enums.UserTypeEnum;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 后台用户信息
 */
@Data
public class JwtUser implements UserDetails {

    /******
     * 后台用户
     * ********/
    private Long userId;
    private String username;
    private String password;
    private Boolean status;
    private UserTypeEnum userTypeEnum;
    private List<GrantedAuthority> perms;
    public JwtUser(Long userId, String username, String password, List<GrantedAuthority> perms, Boolean status) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.perms = perms;
        this.status = status;
        this.userTypeEnum = UserTypeEnum.ADMIN;
    }

    /******
     * app用户
     * ********/
    public JwtUser(Long userId, List<GrantedAuthority> perms, Boolean status) {
        this.userId = userId;
        this.perms = perms;
        this.status = status;
        this.userTypeEnum = UserTypeEnum.APP;
    }

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
