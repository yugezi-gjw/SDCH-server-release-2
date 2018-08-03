package com.varian.oiscn.application.resources;

import com.varian.oiscn.anticorruption.resourceimps.UserAntiCorruptionServiceImp;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.OspLogin;
import lombok.AllArgsConstructor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by gbt1220 on 7/27/2017.
 */
@AllArgsConstructor
public class OspTokenValidateThread extends Thread {

    private Configuration configuration;

    private List<OspLogin> list;

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        UserAntiCorruptionServiceImp userAntiCorruptionServiceImp = new UserAntiCorruptionServiceImp(
                configuration.getFhirServerBaseUri(),
                configuration.getOspAuthenticationWsdlUrl(),
                configuration.getOspAuthorizationWsdlUrl()
        );
        list.forEach(ospLogin -> {
            Date curDate = Calendar.getInstance().getTime();
            userAntiCorruptionServiceImp.validateToken(ospLogin.getToken());
            ospLogin.setLastModifiedDt(curDate);
        });
    }
}
