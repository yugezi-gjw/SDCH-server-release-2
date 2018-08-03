package com.varian.oiscn.application.security;

import com.varian.oiscn.base.user.AuthenticationCache;
import com.varian.oiscn.core.user.UserContext;
import io.dropwizard.auth.Authenticator;

import java.security.Principal;
import java.util.Optional;

/**
 * Created by asharma0 on 12/22/2016.
 */
public class TokenAuthenticator implements Authenticator<String, Principal> {

    private AuthenticationCache cache;

    /**
     * Constructor.<br>
     *
     * @param cache AuthenticationCache
     */
    public TokenAuthenticator(AuthenticationCache cache){
        this.cache = cache;
    }

    /* (non-Javadoc)
     * @see io.dropwizard.auth.Authenticator#authenticate(java.lang.Object)
     */
    @Override
    public Optional<Principal> authenticate(String credentials) {
        UserContext c = cache.get(credentials);
        if(c == null){
            return Optional.empty();
        }
        return Optional.of(c);
    }
}
