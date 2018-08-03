package com.varian.oiscn.base.applicationlaunch;

import com.varian.oiscn.base.applicationlanuch.ApplicationLaunchContentPool;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gbt1220 on 7/27/2017.
 */
public class ApplicationLaunchContentPoolTest {
    @Test
    public void testApplicationLaunchContentPool() {
        ApplicationLaunchContentPool.put("guid", "content");
        Assert.assertEquals("content", ApplicationLaunchContentPool.get("guid"));

        ApplicationLaunchContentPool.remove("guid");
        Assert.assertNull(ApplicationLaunchContentPool.get("guid"));
    }
}
