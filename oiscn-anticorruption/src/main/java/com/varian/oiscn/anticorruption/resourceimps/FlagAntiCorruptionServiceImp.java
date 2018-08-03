package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Flag;
import com.varian.oiscn.anticorruption.assembler.FlagAssembler;
import com.varian.oiscn.anticorruption.converter.EnumFlagQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRFlagInterface;
import com.varian.oiscn.core.pagination.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;

/**
 * Created by fmk9441 on 2017-06-23.
 */
public class FlagAntiCorruptionServiceImp {
    private FHIRFlagInterface fhirFlagInterface;

    /**
     * Default Constructor.<br>
     */
    public FlagAntiCorruptionServiceImp() {
        fhirFlagInterface = new FHIRFlagInterface();
    }

    /**
     * Mark Patient Status Icon.<br>
     *
     * @param patientID Patient Id
     * @param flagCode  Flag Code
     * @return flag result
     */
    public Boolean markPatientStatusIcon(String patientID, String flagCode) {
        Flag flag = FlagAssembler.getFlag(patientID, flagCode);
        String id = fhirFlagInterface.create(flag);
        return StringUtils.isNotEmpty(id);
    }

    /**
     * Unmark Patient Status Icon.<br>
     * @param patientID Patient Id
     * @param flagCode Flag Code
     * @return flag result
     */
    public Boolean unmarkPatientStatusIcon(String patientID, String flagCode) {
        Boolean ret = false;
        Pagination<Flag> flagPagination = getPatientStatusIconList(Arrays.asList(patientID), flagCode);
        if (flagPagination != null && !flagPagination.getLstObject().isEmpty()) {
            ret = fhirFlagInterface.deleteFlag(flagPagination.getLstObject().get(0));
        }
        return ret;
    }

    /**
     * Return Patient Flag List.<br> 
     * @param lstPatient Patient Id List
     * @param flagCode Flag Code
     * @return Patient Flag List
     */
    public Map<String, Boolean> queryPatientListFlag(List<String> lstPatient, String flagCode) {
        Map<String, Boolean> map = new HashMap<>();
        Pagination<Flag> flagPagination = getPatientStatusIconList(lstPatient, flagCode);
        if (flagPagination != null) {
            lstPatient.forEach(patientID -> map.put(patientID, flagPagination.getLstObject().stream().anyMatch(x -> getReferenceValue(x.getSubject()).equals(patientID))));
        }
        return map;
    }

    /**
     * Return Patient Flag.<br> 
     * @param patientID Patient Id
     * @param flagCode Flag Code
     * @return Flag Result
     */
    public Boolean checkPatientStatusIcon(String patientID, String flagCode) {
        Map<String, Boolean> map = queryPatientListFlag(Arrays.asList(patientID), flagCode);
        return map.get(patientID);
    }

    private Pagination<Flag> getPatientStatusIconList(List<String> lstPatientID, String flagCode) {
        if(lstPatientID == null || lstPatientID.isEmpty()){
            return null;
        }
        int countPerPage = Integer.MAX_VALUE;
        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = new LinkedHashMap<>();
        flagQueryImmutablePairMap.put(EnumFlagQuery.FLAG_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, flagCode));
        flagQueryImmutablePairMap.put(EnumFlagQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPatientID));
        Pagination<Flag> pagination = fhirFlagInterface.queryPagingFlagList(flagQueryImmutablePairMap, countPerPage, 1,Integer.MAX_VALUE);
        return pagination;
    }
}