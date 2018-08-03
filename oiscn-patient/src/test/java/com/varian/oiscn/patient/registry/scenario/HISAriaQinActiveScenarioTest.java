package com.varian.oiscn.patient.registry.scenario;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HISAriaQinActiveScenario.class)
public class HISAriaQinActiveScenarioTest {
    @Test
    public void testSaveOrUpdate() {
        Configuration configuration = PowerMockito.mock(Configuration.class);
        HISAriaQinActiveScenario scenario = new HISAriaQinActiveScenario(new Patient(), new Encounter());
        Assert.assertNull(scenario.saveOrUpdate(configuration, new UserContext()));
    }
}
