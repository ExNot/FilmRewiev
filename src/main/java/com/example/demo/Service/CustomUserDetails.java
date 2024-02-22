package com.example.demo.Service;

import com.example.demo.Model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final com.example.demo.Model.User user;
    public CustomUserDetails(User user) {
        this.user = user;

    }

    public String getPPUrl(){
        return user.getPPUrl();
    }
    public Long getId(){
        return user.getId();
    }
    public void setPPUrl(String ppUrl){this.user.setPPUrl(ppUrl);}


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
        return true;
    }


}
