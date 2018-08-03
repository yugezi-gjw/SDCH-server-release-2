package com.varian.oiscn.base.user;

import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.OspLogin;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class AuthenticationCacheTest {

    private AuthenticationCache cache;

    @Before
    public void setup() {
        cache = new AuthenticationCache(10);
    }

    @Test
    public void shouldGetUserContext() {
        UserContext userContext = givenUserContext();
        cache.put("token", userContext);
        UserContext returned = cache.get("token");

        assertThat(returned, is(userContext));
        assertThat(userContext.getLogin(), is(returned.getLogin()));
        assertThat("name", is(returned.getLogin().getName()));
    }

    @Test
    public void shouldRemoveUserContext() {
        UserContext userContext = givenUserContext();
        cache.put("token", userContext);

        cache.remove("token");
        Assert.assertEquals(0, cache.getTokenCache().size());
    }

    private UserContext givenUserContext() {
        Login login = MockDtoUtil.givenALogin();
        OspLogin ospLogin = MockDtoUtil.givenAnOspLogin();
        UserContext userContext = new UserContext(login, ospLogin);
        return userContext;
    }
}
