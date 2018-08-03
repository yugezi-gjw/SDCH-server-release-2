package com.varian.oiscn.application.security;

import com.varian.oiscn.base.user.AuthenticationCache;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.Principal;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by gbt1220 on 12/30/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TokenAuthenticator.class})
public class TokenAuthenticatorTest {

    private AuthenticationCache cache;

    private TokenAuthenticator tokenAuthenticator;

    private String validCredential = "validCredential";

    private String invalidCredential = "invalidCredential";

    @Before
    public void setup() {
        cache = PowerMockito.mock(AuthenticationCache.class);
        tokenAuthenticator = new TokenAuthenticator(cache);
    }

    @Test
    public void givenAValidCredentialWhenAuthenticatedThenReturnUserContext() {
        UserContext userContext = PowerMockito.mock(UserContext.class);
        PowerMockito.when(cache.get(validCredential)).thenReturn(userContext);

        Optional<Principal> result = tokenAuthenticator.authenticate(validCredential);

        assertThat(result.get(), equalTo(userContext));
    }

    @Test
    public void givenAInvalidCredentialWhenNotAuthenticatedThenReturnEmpty() {
        PowerMockito.when(cache.get(invalidCredential)).thenReturn(null);
        Optional<Principal> result = tokenAuthenticator.authenticate(invalidCredential);

        assertThat(result, equalTo(Optional.empty()));
    }
}
