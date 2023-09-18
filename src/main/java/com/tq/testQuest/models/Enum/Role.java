package com.tq.testQuest.models.Enum;

import org.springframework.security.core.GrantedAuthority;
import com.tq.testQuest.models.Enum.Role;


public enum Role implements GrantedAuthority {
    ROLE_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
