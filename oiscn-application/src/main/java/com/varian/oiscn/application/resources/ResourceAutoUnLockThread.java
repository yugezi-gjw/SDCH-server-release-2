package com.varian.oiscn.application.resources;

import com.varian.oiscn.base.tasklocking.TaskLockingDto;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.base.user.AuthenticationCache;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Unlock the expired or logout user's resource Thread.<br>
 */
@Slf4j
public class ResourceAutoUnLockThread implements Runnable {

    /**
     * Authentication Cache
     */
    protected AuthenticationCache cache;

    protected TaskLockingServiceImpl service;

    /**
     * Constructor.<br>
     *
     * @param authenticationCache cache
     */
    public ResourceAutoUnLockThread(TaskLockingServiceImpl taskLockingServiceImpl, AuthenticationCache authenticationCache) {
        this.cache = authenticationCache;
        this.service = taskLockingServiceImpl;
    }

    @Override
    public void run() {
        // get removed user name list from token cache.
        final List<String> removedList = cache.getRemovedUsernameList();
        log.debug("removedList: {}", removedList.toString());

        removedList.forEach(userName -> {
            TaskLockingDto dto = new TaskLockingDto();
            dto.setLockUserName(userName);
            if (service.unLockTask(dto)) {
                log.info("[{}] Resource Unlocked.", userName);
            }
        });
    }
}
