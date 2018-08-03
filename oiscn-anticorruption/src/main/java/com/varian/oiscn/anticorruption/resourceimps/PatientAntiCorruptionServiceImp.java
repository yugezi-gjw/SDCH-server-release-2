package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Patient;
import com.varian.oiscn.anticorruption.assembler.PatientAssembler;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumPatientQuery;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRPatientInterface;
import com.varian.oiscn.cache.PatientCache;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.patient.PatientRankEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

/**
 * Created by fmk9441 on 12/29/2016.
 */
public class PatientAntiCorruptionServiceImp {
    private FHIRPatientInterface fhirPatientInterface;

    /**
     * Default Constructor.<br>
     */
    public PatientAntiCorruptionServiceImp() {
        fhirPatientInterface = new FHIRPatientInterface();
    }

    /**
     * Create Patient.<br>
     *
     * @param patientDto Patient DTO
     * @return new Id
     */
    public String createPatient(PatientDto patientDto) {
        Patient patient = PatientAssembler.getPatient(patientDto);
        return fhirPatientInterface.create(patient);
    }

    /**
     * Update Patient DTO.<br>
     *
     * @param patientDto Patient DTO
     * @return id
     */
    public String updatePatient(PatientDto patientDto) {
        String updatedPatientId = StringUtils.EMPTY;
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.HIS_ID_WITH_PHOTO, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientDto.getHisId()));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        if (!lstPatient.isEmpty()) {
            PatientAssembler.updatePatient(lstPatient.get(0), patientDto);
            updatedPatientId = fhirPatientInterface.update(lstPatient.get(0));
//          After update patientDto,remove it from cache.So when query patient again,query it from fhir and then put it into PatientCache.
//          In cache,the dto is the lasted new.
            PatientCache.remove(patientDto.getPatientSer());
        }
        return updatedPatientId;
    }

    /**
     * Update Patient DTO with PatientSer.<br>
     * @param patientSer Patient Serial No
     * @param patientDto Patient DTO
     * @return id
     */
    public String update(Long patientSer, PatientDto patientDto) {
        String updatedPatientId = StringUtils.EMPTY;
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID_WITH_PHOTO, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientSer));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        if (lstPatient != null && lstPatient.size() > 0) {
            Patient patient = lstPatient.get(0);
            PatientAssembler.updatePatient(patient, patientDto);
            updatedPatientId = fhirPatientInterface.update(patient);
//          After update patientDto,remove it from cache.So when query patient again,query it from fhir and then put it into PatientCache.
//          In cache,the dto is the lasted new.
            PatientCache.remove(patientDto.getPatientSer());
        }
        return updatedPatientId;
    }

    /**
     * Return Patient DTO.<br>
     *
     * @param hisId Patient HIS Id
     * @return
     */
    public PatientDto queryPatientByHisId(String hisId) {
        PatientDto patientDto = null;
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.HIS_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, hisId));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        if (!lstPatient.isEmpty()) {
            patientDto = PatientAssembler.getPatientDto(lstPatient.get(0));
        }

        return patientDto;
    }

    /**
     * Return Patient DTO.<br>
     *
     * @param hisId Patient HIS Id
     * @return
     */
    public PatientDto queryPatientWithPhotoByHisId(String hisId) {
        PatientDto patientDto = null;
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.HIS_ID_WITH_PHOTO, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, hisId));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        if (!lstPatient.isEmpty()) {
            patientDto = PatientAssembler.getPatientDto(lstPatient.get(0));
        }

        return patientDto;
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @param hisId             Patient HIS Id
     * @param activeStatusCode  Active Status Code
     * @param urgentCode        Urgent Code
     * @param lstPractitionerId Practitioner Id List
     * @return Patient DTO List
     */
    public List<PatientDto> queryPatientByHisIdAndActiveStatusAndPractitionerIds(String hisId, String activeStatusCode, String urgentCode, List<String> lstPractitionerId) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        if (lstPractitionerId != null && !lstPractitionerId.isEmpty()) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPractitionerId));
        }
        if (StringUtils.isNotEmpty(hisId)) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.HIS_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, hisId));
        }
        if (StringUtils.isNotEmpty(activeStatusCode)) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        }
        if (StringUtils.isNotEmpty(urgentCode)){
            patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));
        }
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        return getPatientDtos(lstPatient);
    }

    /**
     * Return Patient DTO.<br>
     *
     * @param ariaId Patient Aria Id
     * @return Patient DTO
     */
    public PatientDto queryPatientByAriaId(String ariaId) {
        PatientDto patientDto = null;
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.ARIA_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ariaId));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        if (!lstPatient.isEmpty()) {
            patientDto = PatientAssembler.getPatientDto(lstPatient.get(0));
        }
        return patientDto;
    }

    /**
     * Return Patient DTO List by Aria Id.<br>
     *
     * @param ariaId Patient Aria Id
     * @return Patient DTO List
     */
    public List<PatientDto> queryByAriaId(String ariaId) {
        List<PatientDto> dtoList = new ArrayList<>();
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.ARIA_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ariaId));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        PatientDto dto;
        for (Patient p: lstPatient) {
            dto = PatientAssembler.getPatientDto(p);
            dtoList.add(dto);
        }
        return dtoList;
    }
    
    /**
     * Return Patient DTO List.<br>
     *
     * @param ariaId            Patient Aria Id
     * @param activeStatusCode  Active Status Code
     * @param urgentCode        Urgent Code
     * @param lstPractitionerId Practitioner Id List
     * @return Patient DTO List
     */
    public List<PatientDto> queryPatientByAriaIdAndActiveStatusAndUrgentAndPractitionerIds(String ariaId, String activeStatusCode, String urgentCode, List<String> lstPractitionerId) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        if (lstPractitionerId != null && !lstPractitionerId.isEmpty()) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPractitionerId));
        }
        if (StringUtils.isNotEmpty(ariaId)) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.ARIA_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, ariaId));
        }
        if (StringUtils.isNotEmpty(activeStatusCode)) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        }
        if (StringUtils.isNotEmpty(urgentCode)) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));
        }
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        return getPatientDtos(lstPatient);
    }

    /**
     * Return Patient DTO.<br>
     *
     * @param patientId Patient Id
     * @return Patient DTO
     */
    public PatientDto queryPatientByPatientId(String patientId) {
        PatientDto patientDto = null;
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        if (!lstPatient.isEmpty()) {
            patientDto = PatientAssembler.getPatientDto(lstPatient.get(0));
            PatientCache.put(patientId, patientDto);
        }
        return patientDto;
    }

    public PatientDto queryPatientByPatientIdWithPhoto(String patientId) {
        PatientDto patientDto = null;
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID_WITH_PHOTO, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        if (!lstPatient.isEmpty()) {
            patientDto = PatientAssembler.getPatientDto(lstPatient.get(0));
            PatientCache.put(patientId, patientDto);
        }
        return patientDto;
    }

    /**
     * Return Patient Map with Patient Id.<br>
     *
     * @param lstPatientId Patient Id List
     * @return Patient DTO Map
     */
    public Map<String, PatientDto> queryPatientListByPatientIdList(List<String> lstPatientId) {
        if (lstPatientId == null || lstPatientId.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, PatientDto> patientDtoMap = new HashMap<>();
            Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
            patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID_LIST, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPatientId));
            Pagination<Patient> patientPagination = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
            if (patientPagination != null) {
                patientPagination.getLstObject().forEach(patient -> {
                    patientDtoMap.put(patient.getIdElement().getIdPart(), PatientAssembler.getPatientDto(patient));
                    PatientCache.put(patient.getIdElement().getIdPart(), patientDtoMap.get(patient.getIdElement().getIdPart()));
                });
            }
        return patientDtoMap;
    }

    public void syncPatientListByPatientIdList(List<String> lstPatientId) {
        this.queryPatientListByPatientIdList(lstPatientId);
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @param patientName Patient Name
     * @return Patient DTO List
     */
    public List<PatientDto> queryPatientListByPatientName(String patientName) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_NAME, new ImmutablePair<>(EnumMatchQuery.MATCHES, patientName));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, PatientRankEnum.getDisplay(PatientRankEnum.CREATION_DATE)));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        return getPatientDtos(lstPatient);
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @param patientName       Patient Name
     * @param activeStatusCode  Active Status Code
     * @param lstPractitionerId Practitioner Id List
     * @param countPerPage      Count Per Page
     * @param pageNumberFrom    Page Number From
     * @param pageNumberTo      Page Number To
     * @return Patient DTO List
     */
    public Pagination<PatientDto> queryPatientPaginationListByPatientNameAndPractitionerIds(String patientName, String activeStatusCode, List<String> lstPractitionerId, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        if (lstPractitionerId != null && !lstPractitionerId.isEmpty()) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPractitionerId));
        }
        if (StringUtils.isNotEmpty(patientName)) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_NAME, new ImmutablePair<>(EnumMatchQuery.MATCHES, patientName));
        }
        if (StringUtils.isNotEmpty(activeStatusCode)) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        }
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, PatientRankEnum.getDisplay(PatientRankEnum.CREATION_DATE)));
        Pagination<Patient> pagination = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPatientDtoPagination(pagination);
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @param pinyin            Pinyin
     * @param activeStatusCode  Active Status Code
     * @param lstPractitionerId Practitioner Id List
     * @param countPerPage      Count Per Page
     * @param pageNumberFrom    Page Number From
     * @param pageNumberTo      Page Number To
     * @return Patient DTO List
     */
    public Pagination<PatientDto> queryPatientPaginationListByPatientPinyinAndPractitionerIds(String pinyin, String activeStatusCode, List<String> lstPractitionerId, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        if (lstPractitionerId != null && !lstPractitionerId.isEmpty()) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPractitionerId));
        }
        if (StringUtils.isNotEmpty(pinyin)) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_PINYIN, new ImmutablePair<>(EnumMatchQuery.MATCHES, pinyin));
        }
        if (StringUtils.isNotEmpty(activeStatusCode)) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        }
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, PatientRankEnum.getDisplay(PatientRankEnum.CREATION_DATE)));
        Pagination<Patient> pagination = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPatientDtoPagination(pagination);
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @param lstPractitionerId Practitioner Id List
     * @return Patient DTO List
     */
    public List<PatientDto> queryPatientDtoListByPractitionerIdList(List<String> lstPractitionerId) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPractitionerId));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, PatientRankEnum.getDisplay(PatientRankEnum.CREATION_DATE)));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        return getPatientDtos(lstPatient);
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @param lstPractitionerId Practitioner Id List
     * @param countPerPage      Count Per Page
     * @param pageNumberFrom    Page Number From
     * @param pageNumberTo      Page Number To
     * @return Patient DTO List
     */
    public Pagination<PatientDto> queryPatientDtoPaginationByPractitionerIdList(List<String> lstPractitionerId, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPractitionerId));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, PatientRankEnum.getDisplay(PatientRankEnum.CREATION_DATE)));
        Pagination<Patient> patientPagination = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPatientDtoPagination(patientPagination);
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @param lstPractitionerId Practitioner Id List
     * @param countPerPage      Count Per Page
     * @param pageNumberFrom    Page Number From
     * @param pageNumberTo      Page Number To
     * @param activeStatusCode  Active Status Code
     * @param urgentCode        Urgent Code
     * @return Patient DTO List
     */
    public Pagination<PatientDto> queryPatientDtoPaginationByPractitionerIdList(List<String> lstPractitionerId, int countPerPage, int pageNumberFrom, int pageNumberTo, String activeStatusCode, String urgentCode) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstPractitionerId));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        if (urgentCode != null) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));
        }
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, PatientRankEnum.getDisplay(PatientRankEnum.CREATION_DATE)));
        Pagination<Patient> patientPagination = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPatientDtoPagination(patientPagination);
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @return Patient DTO List
     */
    public List<PatientDto> queryAllActivePatients() {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, PatientRankEnum.getDisplay(PatientRankEnum.CREATION_DATE)));
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        return getPatientDtos(lstPatient);
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @param countPerPage   Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo   Page Number To
     * @return Patient DTO List
     */
    public Pagination<PatientDto> queryAllPatientsWithPaging(int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, PatientRankEnum.getDisplay(PatientRankEnum.CREATION_DATE)));
        Pagination<Patient> patientPagination = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPatientDtoPagination(patientPagination);
    }

    /**
     * Return Patient DTO List.<br>
     *
     * @param countPerPage     Count Per Page
     * @param pageNumberFrom   Page Number From
     * @param pageNumberTo     Page Number To
     * @param activeStatusCode Active Status Code
     * @return Patient DTO List
     */
    public Pagination<PatientDto> queryAllPatientsWithPaging(int countPerPage, int pageNumberFrom, int pageNumberTo, String activeStatusCode, String urgentCode) {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activeStatusCode));
        if (urgentCode != null) {
            patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, urgentCode));
        }
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, PatientRankEnum.getDisplay(PatientRankEnum.CREATION_DATE)));
        Pagination<Patient> patientPagination = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPatientDtoPagination(patientPagination);
    }

    private List<PatientDto> getPatientDtos(List<Patient> lstPatient) {
        List<PatientDto> lstPatientDto = new ArrayList<>();
        if (!lstPatient.isEmpty()) {
            lstPatient.forEach(patient -> lstPatientDto.add(PatientAssembler.getPatientDto(patient)));
        }

        return lstPatientDto;
    }

    private Pagination<PatientDto> getPatientDtoPagination(Pagination<Patient> patientPagination) {
        Pagination<PatientDto> patientDtoPagination = new Pagination<>();
        if (patientPagination != null) {
            patientDtoPagination.setTotalCount(patientPagination.getTotalCount());
            patientDtoPagination.setLstObject(getPatientDtos(patientPagination.getLstObject()));
        }
        return patientDtoPagination;
    }
}