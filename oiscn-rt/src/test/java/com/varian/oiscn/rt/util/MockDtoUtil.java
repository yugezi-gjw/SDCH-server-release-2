package com.varian.oiscn.rt.util;

import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.OspLogin;
import com.varian.oiscn.core.user.UserContext;

import java.util.Date;

/**
 * Created by gbt1220 on 5/26/2017.
 * Modified by bhp9696 on 7/6/2017.
 */
public class MockDtoUtil {
    private MockDtoUtil() {
    }

    public static UserContext givenUserContext() {
        return new UserContext(givenALogin(), givenAnOspLogin());
    }

    public static Login givenALogin() {
        Login login = new Login();
        login.setGroup("group");
        login.setName("name");
        login.setResourceSer(1L);
        login.setToken("token");
        login.setUsername("username");
        return login;
    }


    public static OspLogin givenAnOspLogin() {
        OspLogin ospLogin = new OspLogin();
        ospLogin.setName("name");
        ospLogin.setUsername("username");
        ospLogin.setDisplayName("displayName");
        ospLogin.setUserCUID("cuid");
        ospLogin.setToken("token");
        ospLogin.setLastModifiedDt(new Date());
        return ospLogin;
    }

}
