package com.varian.oiscn.resource;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class BaseResponseTest {

    @Test
    public void testAddError() {
        BaseResponse res = new BaseResponse();
        String errId = "errId";
        Object errItem = PowerMockito.mock(Object.class);
        res.addError(errId, errItem);

        List<Map<String, Object>> errors = res.getErrors();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        Map<String, Object> error = errors.get(0);
        assertEquals(errId, error.get("id"));
        assertEquals(errItem, error.get("item"));
    }

    @Test
    public void testSetterAndGetter() {
        BaseResponse res = new BaseResponse();
        Object data = PowerMockito.mock(Object.class);
        String status = PowerMockito.mock(String.class);
        String msg = PowerMockito.mock(String.class);

        res.setData(data);
        res.setStatus(status);
        res.setMsg(msg);

        assertSame(res.getData(), data);
        assertSame(res.getStatus(), status);
        assertSame(res.getMsg(), msg);
    }
}
