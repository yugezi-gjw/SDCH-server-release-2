package com.varian.oiscn.application.resources;

import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.base.user.AuthenticationCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthenticationCache.class, ResourceAutoUnLockThread.class, TaskLockingServiceImpl.class})
public class ResourceAutoUnLockThreadTest {

    @Mock
    protected TaskLockingServiceImpl service;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testResourceAutoUnLockThread() {
        AuthenticationCache cache = PowerMockito.mock(AuthenticationCache.class);
        ResourceAutoUnLockThread th = new ResourceAutoUnLockThread(service, cache);
        Assert.assertEquals(cache, th.cache);
    }

    @Test
    public void testRun() {
        AuthenticationCache cache = PowerMockito.mock(AuthenticationCache.class);
        List<String> usernameList = Arrays.asList("user01", "user02");
        PowerMockito.when(cache.getRemovedUsernameList()).thenReturn(usernameList);

        try {
            TaskLockingServiceImpl service = PowerMockito.mock(TaskLockingServiceImpl.class);
            PowerMockito.whenNew(TaskLockingServiceImpl.class).withAnyArguments().thenReturn(service);

            ResourceAutoUnLockThread th = new ResourceAutoUnLockThread(service, cache);
            th.run();
            Mockito.verify(service, Mockito.times(usernameList.size())).unLockTask(Mockito.any());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
