package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Patient;
import com.varian.oiscn.anticorruption.assembler.PatientAssembler;
import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumPatientQuery;
import com.varian.oiscn.anticorruption.datahelper.MockPatientUtil;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRPatientInterface;
import com.varian.oiscn.cache.PatientCache;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.patient.PatientDto;
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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;

/**
 * Created by gbt1220 on 12/24/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientAntiCorruptionServiceImp.class, PatientAssembler.class, FHIRPatientInterface.class, PatientIdMapper.class})
public class PatientAntiCorruptionServiceImpTest {
    private static final String PATIENT_ID = "PatientId";
    private FHIRPatientInterface fhirPatientInterface;
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    @Before

    public void setup() throws Exception {
        fhirPatientInterface = PowerMockito.mock(FHIRPatientInterface.class);
        PowerMockito.whenNew(FHIRPatientInterface.class).withNoArguments().thenReturn(fhirPatientInterface);
        patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        PowerMockito.mockStatic(PatientIdMapper.class);
        PowerMockito.when(PatientIdMapper.getPatientId1Mapper()).thenReturn(PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID);
        PowerMockito.when(PatientIdMapper.getPatientId2Mapper()).thenReturn(PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID);
    }

    @Test
    public void givenAPatientDtoWhenCreatePatientThenReturnPatientId() {
        Patient patient = PowerMockito.mock(Patient.class);
        PatientDto patientDto = PowerMockito.mock(PatientDto.class);
        PowerMockito.mockStatic(PatientAssembler.class);
        PowerMockito.when(PatientAssembler.getPatient(patientDto)).thenReturn(patient);
        PowerMockito.when(fhirPatientInterface.create(patient)).thenReturn(PATIENT_ID);
        String createdPatientId = patientAntiCorruptionServiceImp.createPatient(patientDto);
        Assert.assertEquals(createdPatientId, PATIENT_ID);
    }

    @Test
    public void testUpdate() {
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        Long patientSer = Long.parseLong(patientDto.getPatientSer());
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID_WITH_PHOTO, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientSer));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        PowerMockito.when(fhirPatientInterface.update(lstPatient.get(0))).thenReturn(PATIENT_ID);
        String updatedPatientId = patientAntiCorruptionServiceImp.update(patientSer, patientDto);
        Assert.assertEquals("PatientId", updatedPatientId);
        Assert.assertEquals(PATIENT_ID, updatedPatientId);
    }
        
    @Test
    public void givenAPatientDtoWhenUpdatePatientThenReturnPatientId() {
        PatientDto patientDto = MockPatientUtil.givenAPatientDto();
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.HIS_ID_WITH_PHOTO, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientDto.getHisId()));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        PowerMockito.when(fhirPatientInterface.update(lstPatient.get(0))).thenReturn(PATIENT_ID);
        String updatedPatientId = patientAntiCorruptionServiceImp.updatePatient(patientDto);
        Assert.assertEquals("PatientId", updatedPatientId);
        Assert.assertEquals(updatedPatientId, PATIENT_ID);
    }

    @Test
    public void givenAHisIdWhenQueryPatientThenReturnPatientDto() {
        final String hisId = "HisId";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.HIS_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, hisId));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        PatientDto patientDto = patientAntiCorruptionServiceImp.queryPatientByHisId(hisId);
        Assert.assertThat(patientDto, is(notNullValue()));
        Assert.assertEquals(patientDto.getHisId(), "HISID");
    }

    @Test
    public void testQueryPatientWithPhotoByHisId() {
        final String hisId = "HisId";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID_WITH_PHOTO, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, hisId));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        PatientDto patientDto = patientAntiCorruptionServiceImp.queryPatientByPatientIdWithPhoto(hisId);
        Assert.assertThat(patientDto, is(notNullValue()));
        Assert.assertEquals(patientDto.getHisId(), "HISID");
    }

    @Test
    public void givenAnAriaIdWhenQueryPatientThenReturnPatientDto() {
        final String ariaId = "AriaId";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.ARIA_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ariaId));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        PatientDto patientDto = patientAntiCorruptionServiceImp.queryPatientByAriaId(ariaId);
        Assert.assertThat(patientDto, is(notNullValue()));
        Assert.assertEquals(patientDto.getAriaId(), "ARIAID");
    }

    @Test
    public void queryByAriaIdTest() {
        final String ariaId = "AriaId";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.ARIA_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ariaId));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        List<PatientDto> dtoList = patientAntiCorruptionServiceImp.queryByAriaId(ariaId);
        Assert.assertEquals(1, dtoList.size());
        Assert.assertThat(dtoList.get(0), is(notNullValue()));
        Assert.assertEquals(dtoList.get(0).getAriaId(), "ARIAID");
    }
    
    @Test
    public void givenAnAriaIdAndActivityStatusWhenQueryPatientThenReturnPatientDto() {
        final String ariaId = "AriaId";
        final String activeStatusCode = "3";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.ARIA_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ariaId));
        patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        List<PatientDto> patientDtoList = patientAntiCorruptionServiceImp.queryPatientByAriaIdAndActiveStatusAndUrgentAndPractitionerIds(ariaId, activeStatusCode, null, null);
        Assert.assertThat(patientDtoList, is(notNullValue()));
        Assert.assertEquals(patientDtoList.get(0).getAriaId(), "ARIAID");
    }

    @Test
    public void givenAPatientIdWhenQueryPatientThenReturnPatientDto() {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, PATIENT_ID));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        PatientDto patientDto = patientAntiCorruptionServiceImp.queryPatientByPatientId(PATIENT_ID);
        Assert.assertThat(patientDto, is(notNullValue()));
        Assert.assertEquals(patientDto.getHisId(), "HISID");
    }

    @Test
    public void givenAPatientIdListWhenQueryPatientThenReturnPatientDtoList() {
        final List<String> patientIdList = Arrays.asList("PatientID");
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID_LIST, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientIdList));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        Pagination<Patient> patientPagination = new Pagination<>();
        patientPagination.setTotalCount(5);
        patientPagination.setLstObject(lstPatient);
        PowerMockito.when(fhirPatientInterface.queryPagingPatientList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(patientPagination);
        Map<String, PatientDto> patientDtoMap = patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(patientIdList);
        Assert.assertThat(1, is(patientDtoMap.size()));
    }

    @Test
    public void givenAPatientNameWhenQueryPatientThenReturnPatientDtoList() {
        final String patientName = "PatientName";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_NAME, new ImmutablePair<>(EnumMatchQuery.MATCHES, patientName));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        List<PatientDto> lstPatientDto = patientAntiCorruptionServiceImp.queryPatientListByPatientName(patientName);
        Assert.assertThat(1, is(lstPatientDto.size()));
    }

    @Test
    public void givenAnPatientNameWhenQueryPatientPagingThenReturnPatientDtoList() {
        final String patientName = "PatientName";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_NAME, new ImmutablePair<>(EnumMatchQuery.MATCHES, patientName));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        Pagination<Patient> pagination = new Pagination<>();
        pagination.setLstObject(lstPatient);
        pagination.setTotalCount(lstPatient.size());
        PowerMockito.when(fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, 30, 1,1)).thenReturn(pagination);
        Pagination<PatientDto> lstPatientDto = patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientNameAndPractitionerIds(patientName, null, null, 30, 1,1);
        Assert.assertThat(1, is(lstPatientDto.getLstObject().size()));
    }

    @Test
    public void givenAnPatientPinyinWhenQueryPatientPagingThenReturnPatientDtoList() {
        final String patientPinyinName = "PY";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_PINYIN, new ImmutablePair<>(EnumMatchQuery.MATCHES, patientPinyinName));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        Pagination<Patient> pagination = new Pagination<>();
        pagination.setLstObject(lstPatient);
        pagination.setTotalCount(lstPatient.size());
        PowerMockito.when(fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, 30, 1,1)).thenReturn(pagination);
        Pagination<PatientDto> lstPatientDto = patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientPinyinAndPractitionerIds(patientPinyinName, null, null, 30, 1,1);
        Assert.assertThat(1, is(lstPatientDto.getLstObject().size()));
    }


    @Test
    public void givenAnPatientNameWhenQueryPatientPaginationListByPatientNameThenReturnPagination() {
        final String patientName = "PatientName";
        final String activeStatusCode = "activeStatusCode";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_NAME, new ImmutablePair<>(EnumMatchQuery.MATCHES, patientName));
        patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        Pagination<Patient> paging = new Pagination<>();
        paging.setLstObject(lstPatient);
        paging.setTotalCount(1);
        PowerMockito.when(fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, 10, 1,1)).thenReturn(paging);
        Pagination<PatientDto> pagination = patientAntiCorruptionServiceImp.queryPatientPaginationListByPatientNameAndPractitionerIds(patientName, activeStatusCode, null, 10, 1,1);
        Assert.assertThat(1, is(pagination.getLstObject().size()));
    }

    @Test
    public void givenAPractitionerIdWhenQueryPatientThenReturnPatientDtoList() {
        final String practitionerId = "PractitionerId";
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(practitionerId)));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap)).thenReturn(lstPatient);
        List<PatientDto> lstPatientDto = patientAntiCorruptionServiceImp.queryPatientDtoListByPractitionerIdList(Arrays.asList(practitionerId));
        Assert.assertThat(1, is(lstPatientDto.size()));
    }

    @Test
    public void givenAPractitionerIdWithPagingWhenQueryPatientThenReturnPatientDtoPagination() {
        final List<String> lstPractitionerId = Arrays.asList("PractitionerId");
        final Integer countPerPage = 10;
        final Integer pageNumber = 2;
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPractitionerId));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        Pagination<Patient> patientPagination = new Pagination<>();
        patientPagination.setTotalCount(100);
        patientPagination.setLstObject(lstPatient);
        PowerMockito.when(fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, countPerPage, pageNumber,1)).thenReturn(patientPagination);
        Pagination<PatientDto> patientDtoPagination = patientAntiCorruptionServiceImp.queryPatientDtoPaginationByPractitionerIdList(lstPractitionerId, countPerPage, pageNumber,1);
        Assert.assertThat(patientDtoPagination.getTotalCount(), is(100));
        Assert.assertThat(patientDtoPagination.getLstObject().size(), is(1));
    }

    @Test
    public void testQueryPatientDtoPaginationByPractitionerIdList() {
        final List<String> lstPractitionerId = Arrays.asList("PractitionerId");
        final Integer countPerPage = 10;
        final Integer pageNumber = 2;
        String activeStatusCode = "activeStatusCode";
        String urgentCode = "urgentCode";

        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPractitionerId));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));

        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        Pagination<Patient> patientPagination = new Pagination<>();
        patientPagination.setTotalCount(100);
        patientPagination.setLstObject(lstPatient);
        PowerMockito.when(fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, countPerPage, pageNumber,1)).thenReturn(patientPagination);

        Pagination<PatientDto> patientDtoPagination = patientAntiCorruptionServiceImp.queryPatientDtoPaginationByPractitionerIdList(lstPractitionerId, countPerPage, pageNumber,1, activeStatusCode, urgentCode);
        Assert.assertThat(patientDtoPagination.getTotalCount(), is(100));
        Assert.assertThat(patientDtoPagination.getLstObject().size(), is(1));
    }

    @Test
    public void doQueryPatientThenReturnAllActivePatientDtoList() {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Status"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        PowerMockito.when(fhirPatientInterface.queryPatientList(anyObject())).thenReturn(lstPatient);
        List<PatientDto> lstPatientDto = patientAntiCorruptionServiceImp.queryAllActivePatients();
        Assert.assertThat(1, is(lstPatientDto.size()));
    }

    @Test
    public void doQueryPatientThenReturnPatientDtoListPagination() {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Status"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        Pagination<Patient> patientPagination = new Pagination<>();
        patientPagination.setTotalCount(100);
        patientPagination.setLstObject(lstPatient);
        PowerMockito.when(fhirPatientInterface.queryPagingPatientList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(patientPagination);
        Pagination<PatientDto> patientDtoPagination = patientAntiCorruptionServiceImp.queryAllPatientsWithPaging(5, 1,2);
        Assert.assertThat(patientDtoPagination.getTotalCount(), is(100));
        Assert.assertThat(patientDtoPagination.getLstObject().size(), is(1));
    }

    @Test
    public void testQueryAllPatientsWithPaging() {
        String activeStatusCode = "activeStatusCode";
        String urgentCode = "urgentCode";

        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Status"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));

        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        Pagination<Patient> patientPagination = new Pagination<>();
        patientPagination.setTotalCount(100);
        patientPagination.setLstObject(lstPatient);
        PowerMockito.when(fhirPatientInterface.queryPagingPatientList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(patientPagination);
        Pagination<PatientDto> patientDtoPagination = patientAntiCorruptionServiceImp.queryAllPatientsWithPaging(5, 1,2, activeStatusCode, urgentCode);
        Assert.assertThat(patientDtoPagination.getTotalCount(), is(100));
        Assert.assertThat(patientDtoPagination.getLstObject().size(), is(1));
    }

    @Test
    public void givenHisIdAndActiveStatusAndPractitionerIdsWhenQueryPatientByHisIdAndActiveStatusAndPractitionerIdsThenReturnList() {
        List<String> lstPractitionerIdList = Arrays.asList("1101");
        String hisId = "hisId";
        String activeStatusCode = "3";
        List<Patient> lstPatient = Arrays.asList(new Patient(), new Patient());
        PowerMockito.when(fhirPatientInterface.queryPatientList(anyObject())).thenReturn(lstPatient);
        List<PatientDto> result = patientAntiCorruptionServiceImp.queryPatientByHisIdAndActiveStatusAndPractitionerIds(hisId, activeStatusCode, null, lstPractitionerIdList);
        Assert.assertTrue(result.size() == 2);
    }

    @Test
    public void testAPatientIdListWhenSyncPatientThenReturnPatientDtoList() {
        final List<String> patientIdList = Arrays.asList("PatientID");
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID_LIST, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientIdList));
        List<Patient> lstPatient = MockPatientUtil.givenAPatientList();
        Pagination<Patient> patientPagination = new Pagination<>();
        patientPagination.setTotalCount(5);
        patientPagination.setLstObject(lstPatient);
        PowerMockito.when(fhirPatientInterface.queryPagingPatientList(anyObject(), anyInt(), anyInt(), anyInt())).thenReturn(patientPagination);
        patientAntiCorruptionServiceImp.syncPatientListByPatientIdList(patientIdList);
        Assert.assertFalse(PatientCache.allKeys().isEmpty());
    }

}