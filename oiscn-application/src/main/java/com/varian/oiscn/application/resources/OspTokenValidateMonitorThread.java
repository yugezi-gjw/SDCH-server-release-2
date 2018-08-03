package com.varian.oiscn.application.resources;

import com.varian.oiscn.base.user.AuthenticationCache;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.OspLogin;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gbt1220 on 7/24/2017.
 */
@Slf4j
public class OspTokenValidateMonitorThread extends Thread {

    private long sleepTime = 5 * 60 * 1000;

    private AuthenticationCache cache;

    private Configuration configuration;

    /**
     * Constructor.<br>
     *
     * @param cache         AuthenticationCache
     * @param configuration Configuration
     */
    public OspTokenValidateMonitorThread(AuthenticationCache cache, Configuration configuration) {
        this.cache = cache;
        this.configuration = configuration;
        this.sleepTime = configuration.getOspTokenValidationInterval() * 60 * 1000;
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        List<OspLogin> needValidateList = getValidateOspLoginList();
        new OspTokenValidateThread(configuration, needValidateList).start();
    }

    private List<OspLogin> getValidateOspLoginList() {
        List<OspLogin> needValidateList = new ArrayList<>();
        cache.getTokenCache().asMap().forEach((token, userContext) -> {
            OspLogin ospLogin = userContext.getOspLogin();
            long interval = new Date().getTime() - ospLogin.getLastModifiedDt().getTime();
            if (interval >= sleepTime) {
                needValidateList.add(ospLogin);
            }
        });
        return needValidateList;
    }
}
