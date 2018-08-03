package com.varian.oiscn.base.coverage;

import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(PowerMockRunner.class)
@PrepareForTest({PayorInfoResource.class})
public class PayorInfoResourceTest {

    protected Map<String, String> payorMap;

    @Before
    public void setup() {
        payorMap = new HashMap<>();
        payorMap.put("code01", "desc01");
        payorMap.put("code02", "desc02");
        payorMap.put("code03", "desc03");
        payorMap.put("code04", "desc04");
        payorMap.put("code05", "desc05");
        payorMap.put("code06", "desc06");
        payorMap.put("code07", "desc07");
        payorMap.put("code08", "desc08");
        payorMap.put("code09", "desc09");
    }

    @Test
    public void testGetAllPayorInfoNull() {

        PayorInfoPool.getCachedPayorInfo().clear();
        PayorInfoResource resource = new PayorInfoResource(null, null);
        UserContext userContext = PowerMockito.mock(UserContext.class);

        Response res = resource.getAllPayorInfo(userContext);
        // empty
        assertEquals(Response.Status.OK, res.getStatusInfo());
        Assert.assertTrue(((List)res.getEntity()).isEmpty());
    }

    @Test
    public void testGetAllPayorInfoNormal() {

        PayorInfoPool.getCachedPayorInfo().clear();

        for (Entry<String, String> entry : payorMap.entrySet()) {
            PayorInfoPool.put(entry.getKey(), entry.getValue());
        }

        PayorInfoResource resource = new PayorInfoResource(null, null);
        UserContext userContext = PowerMockito.mock(UserContext.class);

        Response res = resource.getAllPayorInfo(userContext);
        // empty
        assertEquals(Response.Status.OK, res.getStatusInfo());

        Object entity = res.getEntity();
        if (entity instanceof List) {
            @SuppressWarnings("unchecked")
            List<KeyValuePair> pairs = (List<KeyValuePair>) entity;
            for (KeyValuePair pair : pairs) {
                String key = pair.getKey();
                String value = pair.getValue();
                assertTrue(payorMap.containsKey(key));
                assertTrue(payorMap.containsValue(value));
            }

        }
    }
}
