package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.varian.fhir.resources.Device;
import com.varian.oiscn.anticorruption.converter.EnumDeviceQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fmk9441 on 2017-02-14.
 */
@Slf4j
public class FHIRDeviceInterface extends FHIRInterface<Device> {

    /**
     * Return Fhir Device List by Device Query Immutable PairMap.<br>
     *
     * @param deviceQueryImmutablePairMap Device Query Immutable PairMap
     * @return Fhir Device List
     */
    public List<Device> queryDeviceList(Map<EnumDeviceQuery, ImmutablePair<EnumMatchQuery, Object>> deviceQueryImmutablePairMap) {
        List<Device> lstDevice = new ArrayList<>();
        if (null == deviceQueryImmutablePairMap || deviceQueryImmutablePairMap.isEmpty()) {
            return lstDevice;
        }

        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        try {
            IQuery<?> iQuery = client.search().forResource(Device.class);
            for (EnumDeviceQuery enumDeviceQuery : deviceQueryImmutablePairMap.keySet()) {
                ImmutablePair<EnumMatchQuery, Object> enumMatchQueryObjectImmutablePair = deviceQueryImmutablePairMap.get(enumDeviceQuery);
                String params = enumMatchQueryObjectImmutablePair.getRight().toString();
                switch (enumDeviceQuery) {
                    case ID:
                        iQuery = iQuery.where(new StringClientParam(Device.SP_RES_ID).matchesExactly().value(params));
                        break;
                    case TYPE:
                        iQuery = iQuery.where(new StringClientParam(Device.SP_TYPE).matchesExactly().value(params));
                        break;
                    case CODE:
                        iQuery = iQuery.where(new StringClientParam(Device.SP_DEVICE_ID).matchesExactly().values(params));
                        break;
                    default:
                        break;
                }
            }
            long time1 = System.currentTimeMillis();
            Bundle bundle = iQuery.returnBundle(Bundle.class).execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - DeviceResource - QueryDeviceList : {}", (time2 - time1) / 1000.0);
            lstDevice = getListFromBundle(bundle);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        return lstDevice;
    }

}