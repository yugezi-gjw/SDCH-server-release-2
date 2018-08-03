package com.varian.oiscn.core.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserContext implements Principal {
    private Login login;
    private OspLogin ospLogin;

    @Override
    public String getName() {
        return login.getUsername();
    }
}
