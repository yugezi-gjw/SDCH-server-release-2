package com.varian.oiscn.patient.integration.demo;

import com.varian.oiscn.base.codesystem.CodeSystemServiceImp;
import com.varian.oiscn.base.diagnosis.BodyPartVO;
import com.varian.oiscn.core.patient.RegistrationVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bhp9696 on 2018/3/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MockHISPatientQuery.class)
public class MockHISPatientQueryTest {
    private MockHISPatientQuery mockHISPatientQuery;

    @Before
    public void setup(){
        mockHISPatientQuery = new MockHISPatientQuery();
    }

    @Test
    public void testQueryByHisId() throws Exception {
        String hisId = "hisId";
        MockHISPatientQueryConfigService mockHISPatientQueryConfigService = PowerMockito.mock(MockHISPatientQueryConfigService.class);
        PowerMockito.whenNew(MockHISPatientQueryConfigService.class).withAnyArguments().thenReturn(mockHISPatientQueryConfigService);
        List<MockHISPatientQueryDto> list = Arrays.asList(new MockHISPatientQueryDto(){{
            setHisId(hisId);
        }});
        PowerMockito.when(mockHISPatientQueryConfigService.getDemoData()).thenReturn(list);
        CodeSystemServiceImp codeSystemServiceImp = PowerMockito.mock(CodeSystemServiceImp.class);
        PowerMockito.whenNew(CodeSystemServiceImp.class).withAnyArguments().thenReturn(codeSystemServiceImp);
        PowerMockito.when(codeSystemServiceImp.queryBodyParts(Matchers.anyString(),Matchers.anyString())).thenReturn(Arrays.asList(new BodyPartVO("C1.10","Fei","F")));
        RegistrationVO registrationVO = mockHISPatientQuery.queryByHisId(hisId);
        Assert.assertTrue(registrationVO.getHisId().equals(hisId));
    }
}
