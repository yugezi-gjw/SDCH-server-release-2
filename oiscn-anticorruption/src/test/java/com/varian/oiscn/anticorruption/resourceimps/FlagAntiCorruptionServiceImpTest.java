package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Flag;
import com.varian.oiscn.anticorruption.assembler.FlagAssembler;
import com.varian.oiscn.anticorruption.converter.EnumFlagQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.datahelper.MockFlagUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRFlagInterface;
import com.varian.oiscn.core.pagination.Pagination;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;

/**
 * Created by fmk9441 on 2017-06-23.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FlagAntiCorruptionServiceImp.class, FlagAssembler.class})
public class FlagAntiCorruptionServiceImpTest {
    private static final String PATIENT_ID = "PatientID";
    private static final String FLAG_CODE = "FlagCode";
    private FHIRFlagInterface fhirFlagInterface;
    private FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        fhirFlagInterface = PowerMockito.mock(FHIRFlagInterface.class);
        PowerMockito.whenNew(FHIRFlagInterface.class).withNoArguments().thenReturn(fhirFlagInterface);

        flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
    }

    @Test
    public void givenAPatientIdAndFlagCodeWhenMarkIconThenReturnStatus() {
        Flag flag = MockFlagUtil.givenAFlag();
        PowerMockito.mockStatic(FlagAssembler.class);
        PowerMockito.when(FlagAssembler.getFlag(anyString(), anyString())).thenReturn(flag);
        PowerMockito.when(fhirFlagInterface.create(anyObject())).thenReturn("1");

        Boolean ret = flagAntiCorruptionServiceImp.markPatientStatusIcon(PATIENT_ID, FLAG_CODE);
        Assert.assertTrue(ret);
    }

    @Test
    public void givenAPatientIdAndFlagCodeWhenUnmarkIconThenReturnStatus() {
        List<Flag> lstFlag = MockFlagUtil.givenAFlagList();
        Pagination<Flag> flagPagination = new Pagination<>();
        flagPagination.setTotalCount(5);
        flagPagination.setLstObject(lstFlag);
        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = new LinkedHashMap<>();
        flagQueryImmutablePairMap.put(EnumFlagQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(PATIENT_ID)));
        flagQueryImmutablePairMap.put(EnumFlagQuery.FLAG_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, FLAG_CODE));

        PowerMockito.when(fhirFlagInterface.queryPagingFlagList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(flagPagination);
        PowerMockito.when(fhirFlagInterface.deleteFlag(lstFlag.get(0))).thenReturn(true);

        Boolean ret = flagAntiCorruptionServiceImp.unmarkPatientStatusIcon(PATIENT_ID, FLAG_CODE);
        Assert.assertTrue(ret);
    }

    @Test
    public void givenAPatientIdAndFlagCodeWhenCheckIconThenReturnStatus() {
        List<Flag> lstFlag = MockFlagUtil.givenAFlagList();
        Pagination<Flag> flagPagination = new Pagination<>();
        flagPagination.setTotalCount(5);
        flagPagination.setLstObject(lstFlag);
        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = new LinkedHashMap<>();
        flagQueryImmutablePairMap.put(EnumFlagQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(PATIENT_ID)));
        flagQueryImmutablePairMap.put(EnumFlagQuery.FLAG_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, FLAG_CODE));
        PowerMockito.when(fhirFlagInterface.queryPagingFlagList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(flagPagination);

        Boolean ret = flagAntiCorruptionServiceImp.checkPatientStatusIcon(PATIENT_ID, FLAG_CODE);
        Assert.assertTrue(ret);
    }

    @Test
    public void givenAPatientIdListAndFlagCodeWhenCheckIconThenReturnStatus() {
        final List<String> patientIdList = Arrays.asList("PatientID");
        final String flagCode = "FlagCode";

        Map<EnumFlagQuery, ImmutablePair<EnumMatchQuery, Object>> flagQueryImmutablePairMap = new LinkedHashMap<>();
        flagQueryImmutablePairMap.put(EnumFlagQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientIdList));
        flagQueryImmutablePairMap.put(EnumFlagQuery.FLAG_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, flagCode));
        List<Flag> lstFlag = MockFlagUtil.givenAFlagList();
        Pagination<Flag> flagPagination = new Pagination<>();
        flagPagination.setTotalCount(5);
        flagPagination.setLstObject(lstFlag);
        PowerMockito.when(fhirFlagInterface.queryPagingFlagList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(flagPagination);

        Map<String, Boolean> map = flagAntiCorruptionServiceImp.queryPatientListFlag(patientIdList, flagCode);
        Assert.assertTrue(map.get(patientIdList.get(0)).booleanValue());
    }
}