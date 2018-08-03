package com.varian.oiscn.base.codesystem;

import com.varian.oiscn.base.statusicon.StatusIconPool;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Created by gbt1220 on 7/6/2017.
 */
public class StatusIconPoolTest {

    @InjectMocks
    private StatusIconPool statusIconPool;

    @Test
    public void givenWhenPutThenGet() {
        StatusIconPool.put("code", "desc");
        Assert.assertEquals("code", StatusIconPool.get("desc"));
        Assert.assertNull(StatusIconPool.get("notExistedDesc"));
    }
}
