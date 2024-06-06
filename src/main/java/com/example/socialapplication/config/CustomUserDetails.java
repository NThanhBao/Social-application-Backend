package com.example.socialapplication.config;

import com.example.socialapplication.model.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    Users users;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getUsername();
    }

    //        Xác định liệu tài khoản của người dùng có hết hạn hay không.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //        Xác định liệu tài khoản của người dùng có bị khóa hay không.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //        Xác định liệu thông tin đăng nhập (mật khẩu) có hết hạn hay không.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //        Xác định liệu tài khoản của người dùng có được kích hoạt hay không.
    @Override
    public boolean isEnabled() {
        return true;
    }
}
