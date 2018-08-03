package com.varian.oiscn.resource;

import com.varian.oiscn.config.Configuration;
import io.dropwizard.setup.Environment;
import lombok.AllArgsConstructor;

import javax.ws.rs.core.Response;

/**
 * Created by gbt1220 on 1/3/2017.
 */
@AllArgsConstructor
public class AbstractResource {
    protected Configuration configuration;
    protected Environment environment;

    protected long getEncounterId(String inputEncounterId) {
        long encounterId = -1l;
        try {
            encounterId = Long.parseLong(inputEncounterId);
        } catch (Exception e) {

        }
        return encounterId;
    }
    
    protected Response build400Response(Object o) {
        return Response.status(Response.Status.BAD_REQUEST).entity(o).build();
    }
}
