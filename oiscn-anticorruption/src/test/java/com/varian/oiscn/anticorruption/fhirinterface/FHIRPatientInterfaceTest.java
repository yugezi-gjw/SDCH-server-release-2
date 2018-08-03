package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import com.varian.fhir.resources.Patient;
import com.varian.fhir.resources.Practitioner;
import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumPatientQuery;
import com.varian.oiscn.anticorruption.datahelper.MockPatientUtil;
import com.varian.oiscn.anticorruption.exception.FhirCreatePatientException;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.pagination.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IIdType;
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
import static org.mockito.Matchers.*;

/**
 * Created by fmk9441 on 2017-01-17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRPatientInterface.class, FHIRContextFactory.class, CompositeClientParam.class, StringClientParam.class,
        PatientIdMapper.class})
public class FHIRPatientInterfaceTest {
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRPatientInterface fhirPatientInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirPatientInterface = new FHIRPatientInterface();

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void givenAPatientWhenCreateThenReturnPatientId() throws Exception {
        final String patientId = "PatientId";
        Patient patient = MockPatientUtil.givenAPatient();

        ICreate iCreate = PowerMockito.mock(ICreate.class);
        PowerMockito.when(client.create()).thenReturn(iCreate);
        ICreateTyped iCreateTyped = PowerMockito.mock(ICreateTyped.class);
        PowerMockito.when(iCreate.resource(patient)).thenReturn(iCreateTyped);
        MethodOutcome methodOutcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iCreateTyped.execute()).thenReturn(methodOutcome);
        IIdType iIdType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(methodOutcome.getId()).thenReturn(iIdType);
        PowerMockito.when(iIdType.getIdPart()).thenReturn(patientId);

        String createdPatientId = fhirPatientInterface.create(patient);
        Assert.assertEquals(patientId, createdPatientId);
    }

    @Test
    public void givenAPatientWhenCreateThenThrowException() {
        Patient patient = MockPatientUtil.givenAPatient();

        InternalErrorException ie = PowerMockito.mock(InternalErrorException.class);
        PowerMockito.when(ie.getResponseBody()).thenReturn("xxxxSSNNotUniquexxx");
        PowerMockito.when(client.create()).thenThrow(ie);

        String createdPatientId = null;
        try {
            createdPatientId = fhirPatientInterface.create(patient);
            Assert.fail("No Exception");
        } catch (Exception e) {
            if (e instanceof FhirCreatePatientException) {
                FhirCreatePatientException fe = (FhirCreatePatientException) e;
                Assert.assertEquals("nationalId", fe.getErrorItemId());
                Assert.assertNull(createdPatientId);
            } else {
                Assert.fail(e.getMessage());
            }
        }

    }

    @Test
    public void givenAPatientWhenUpdateThenReturnPatientId() throws Exception {
        final String patientId = "PatientId";

        Patient patient = PowerMockito.mock(Patient.class);
        PowerMockito.whenNew(Patient.class).withNoArguments().thenReturn(patient);

        IUpdate iUpdate = PowerMockito.mock(IUpdate.class);
        PowerMockito.when(client.update()).thenReturn(iUpdate);
        IUpdateTyped iUpdateTyped = PowerMockito.mock(IUpdateTyped.class);
        PowerMockito.when(iUpdate.resource(patient)).thenReturn(iUpdateTyped);
        MethodOutcome methodOutcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iUpdateTyped.execute()).thenReturn(methodOutcome);
        IIdType iIdType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(methodOutcome.getId()).thenReturn(iIdType);
        PowerMockito.when(iIdType.getIdPart()).thenReturn(patientId);

        String updatedPatientId = fhirPatientInterface.update(patient);

        Assert.assertEquals(patientId, updatedPatientId);
    }

    @Test
    public void givenAPatientWhenUpdateThenThrowException() throws Exception {
        Patient patient = MockPatientUtil.givenAPatient();
        PowerMockito.when(client.update()).thenThrow(Exception.class);
        String updatedPatientId = fhirPatientInterface.update(patient);
        Assert.assertTrue(StringUtils.isBlank(updatedPatientId));
    }

    @Test
    public void givenANullMapWhenQueryThenReturnEmptyPatientList() {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = null;
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertTrue(lstPatient.isEmpty());
    }

    @Test
    public void givenAnEmptyMapWhenQueryThenReturnEmptyPatientList() {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertTrue(lstPatient.isEmpty());
    }

    @Test
    public void givenAMapWithIDWhenQueryThenReturnPatientList() throws Exception {
        final Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "PatientID"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Patient.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_RES_ID).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);
        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);

        Bundle bundle = MockPatientUtil.givenAPatientBundle();
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);
        PowerMockito.when(iQuery.include(new Include(Patient.INCLUDE_ADDRESS))).thenReturn(iQuery);

        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertThat(2, is(lstPatient.size()));
    }

    @Test
    public void givenAMapWithIDListWhenQueryThenReturnPatientList() throws Exception {
        final Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_ID_LIST, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("PatientID")));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Patient.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_PATIENT_LIST).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.values(anyList())).thenReturn(iCriterion);
        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);

        Bundle bundle = MockPatientUtil.givenAPatientBundle();
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);
        PowerMockito.when(iQuery.include(new Include(Patient.INCLUDE_ADDRESS))).thenReturn(iQuery);

        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertThat(2, is(lstPatient.size()));
    }

    @Test
    public void givenAMapWithHisIDWhenQueryThenReturnPatientList() throws Exception {
        final Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.HIS_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "HisID"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Patient.class)).thenReturn(iQuery);

        CompositeClientParam compositeClientParam = PowerMockito.mock(CompositeClientParam.class);
        PowerMockito.whenNew(CompositeClientParam.class).withArguments(Patient.SP_ID_TYPE_VALUE).thenReturn(compositeClientParam);

        PowerMockito.mockStatic(PatientIdMapper.class);
        PowerMockito.when(PatientIdMapper.getPatientId1Mapper()).thenReturn("hisId");
        PowerMockito.when(PatientIdMapper.getPatientId2Mapper()).thenReturn("ariaId");

        StringClientParam leftStringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_IDType).thenReturn(leftStringClientParam);
        StringClientParam.IStringMatch iStringMatchLeft = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(leftStringClientParam.matchesExactly()).thenReturn(iStringMatchLeft);
        ICriterion iCriterionLeft = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchLeft.value("ARIA ID1")).thenReturn(iCriterionLeft);

        StringClientParam rightStringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_IDValue).thenReturn(rightStringClientParam);
        StringClientParam.IStringMatch iStringMatchRight = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(rightStringClientParam.matchesExactly()).thenReturn(iStringMatchRight);
        ICriterion iCriterionRight = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchRight.value(anyString())).thenReturn(iCriterionRight);
        ICompositeWithLeft iCompositeWithLeft = PowerMockito.mock(ICompositeWithLeft.class);
        PowerMockito.when(compositeClientParam.withLeft(iCriterionLeft)).thenReturn(iCompositeWithLeft);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iCompositeWithLeft.withRight(iCriterionRight)).thenReturn(iCriterion);

        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);

        PowerMockito.when(iQuery.include(anyObject())).thenReturn(iQuery);

        Bundle bundle = MockPatientUtil.givenAPatientBundle();
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertThat(2, is(lstPatient.size()));
    }

    @Test
    public void givenAMapWithAriaIDWhenQueryThenReturnPatientList() throws Exception {
        final Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.ARIA_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "AriaID"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Patient.class)).thenReturn(iQuery);

        CompositeClientParam compositeClientParam = PowerMockito.mock(CompositeClientParam.class);
        PowerMockito.whenNew(CompositeClientParam.class).withArguments(Patient.SP_ID_TYPE_VALUE).thenReturn(compositeClientParam);

        StringClientParam leftStringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_IDType).thenReturn(leftStringClientParam);
        StringClientParam.IStringMatch iStringMatchLeft = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(leftStringClientParam.matchesExactly()).thenReturn(iStringMatchLeft);
        ICriterion iCriterionLeft = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchLeft.value("ARIA ID2")).thenReturn(iCriterionLeft);

        StringClientParam rightStringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_IDValue).thenReturn(rightStringClientParam);
        StringClientParam.IStringMatch iStringMatchRight = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(rightStringClientParam.matchesExactly()).thenReturn(iStringMatchRight);
        ICriterion iCriterionRight = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchRight.value(anyString())).thenReturn(iCriterionRight);

        ICompositeWithLeft iCompositeWithLeft = PowerMockito.mock(ICompositeWithLeft.class);
        PowerMockito.when(compositeClientParam.withLeft(iCriterionLeft)).thenReturn(iCompositeWithLeft);

        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iCompositeWithLeft.withRight(iCriterionRight)).thenReturn(iCriterion);

        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);

        Bundle bundle = MockPatientUtil.givenAPatientBundle();
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);
        PowerMockito.when(iQuery.include(new Include(Patient.INCLUDE_ADDRESS))).thenReturn(iQuery);

        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertThat(2, is(lstPatient.size()));
    }

    @Test
    public void givenAMapWithNameWhenQueryThenReturnPatientList() throws Exception {
        final Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PATIENT_NAME, new ImmutablePair<>(EnumMatchQuery.MATCHES, "PatientName"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Patient.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_FAMILY).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matches()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);
        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);

        Bundle bundle = MockPatientUtil.givenAPatientBundle();
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertThat(2, is(lstPatient.size()));
    }

    @Test
    public void givenAMapWithStatusAndIconFlagWhenQueryThenReturnPatientList() throws Exception {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.ICON_FLAG_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "16"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Patient.class)).thenReturn(iQuery);

        StringClientParam stringClientParamStatus = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_ACTIVE).thenReturn(stringClientParamStatus);
        StringClientParam.IStringMatch iStringMatchStatus = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamStatus.matchesExactly()).thenReturn(iStringMatchStatus);
        ICriterion iCriterionStatus = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchStatus.value(anyString())).thenReturn(iCriterionStatus);
        PowerMockito.when(iQuery.where(iCriterionStatus)).thenReturn(iQuery);

        StringClientParam stringClientParamIconFlag = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_PATIENT_FLAG).thenReturn(stringClientParamIconFlag);
        StringClientParam.IStringMatch iStringMatchIconFlag = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamIconFlag.matchesExactly()).thenReturn(iStringMatchIconFlag);
        ICriterion iCriterionIconFlag = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchIconFlag.value(anyString())).thenReturn(iCriterionIconFlag);
        PowerMockito.when(iQuery.and(iCriterionIconFlag)).thenReturn(iQuery);

        Bundle bundle = MockPatientUtil.givenAPatientBundle();
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertThat(2, is(lstPatient.size()));
    }

    @Test
    public void givenAMapWithStatusAndSortingByCreationDateWhenQueryThenReturnPatientList() throws Exception {
        final Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "CreationDate"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Patient.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_ACTIVE).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);
        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);
        ISort iSort = PowerMockito.mock(ISort.class);
        PowerMockito.when(iQuery.sort()).thenReturn(iSort);
        PowerMockito.when(iSort.descending(anyString())).thenReturn(iQuery);

        Bundle bundle = MockPatientUtil.givenAPatientBundle();
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertThat(2, is(lstPatient.size()));
    }

    @Test
    public void givenAMapWithPrimaryPhysicianAndStatusAndSortByCreationDateWhenQueryThenReturnPatientList() throws Exception {
        final Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.PRIMARY_PHYSICIAN, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("PrimaryPhysician")));
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Status"));
        patientQueryImmutablePairMap.put(EnumPatientQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.ASC, "CreationDate"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Patient.class)).thenReturn(iQuery);

        StringClientParam stringClientParamPhysician = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_PRIMARY_PRACTITIONER).thenReturn(stringClientParamPhysician);
        StringClientParam.IStringMatch iStringMatchPhysician = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamPhysician.matchesExactly()).thenReturn(iStringMatchPhysician);
        ICriterion iCriterionPhysician = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchPhysician.values(anyList())).thenReturn(iCriterionPhysician);
        PowerMockito.when(iQuery.where(iCriterionPhysician)).thenReturn(iQuery);

        StringClientParam stringClientParamStatus = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_ACTIVE).thenReturn(stringClientParamStatus);
        StringClientParam.IStringMatch iStringMatchStatus = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamStatus.matchesExactly()).thenReturn(iStringMatchStatus);
        ICriterion iCriterionStatus = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchStatus.value(anyString())).thenReturn(iCriterionStatus);
        PowerMockito.when(iQuery.and(iCriterionStatus)).thenReturn(iQuery);

        ISort iSort = PowerMockito.mock(ISort.class);
        PowerMockito.when(iQuery.sort()).thenReturn(iSort);
        PowerMockito.when(iSort.ascending(anyString())).thenReturn(iQuery);

        Bundle bundle = MockPatientUtil.givenAPatientBundle();
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Patient> lstPatient = fhirPatientInterface.queryPatientList(patientQueryImmutablePairMap);
        Assert.assertThat(2, is(lstPatient.size()));
    }

    @Test
    public void givenANullMapWhenQueryThenReturnEmptyPatientPagination() throws Exception {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = null;
        Pagination<Patient> patientPagination = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, 5, 10,1);
        Assert.assertNull(patientPagination.getLstObject());
        Assert.assertThat(patientPagination.getTotalCount(), is(0));
    }

    @Test
    public void givenAnEmptyMapWhenQueryThenReturnEmptyPatientPagination() throws Exception {
        Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        Pagination<Patient> patientPagination = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, 5, 10,1);
        Assert.assertNull(patientPagination.getLstObject());
        Assert.assertThat(patientPagination.getTotalCount(), is(0));
    }

    @Test
    public void givenAMapWithStatusWhenQueryThenReturnPagingPatientList() throws Exception {
        final Map<EnumPatientQuery, ImmutablePair<EnumMatchQuery, Object>> patientQueryImmutablePairMap = new LinkedHashMap<>();
        patientQueryImmutablePairMap.put(EnumPatientQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "1"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);
        IQuery<Bundle> iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Patient.class)).thenReturn(iQuery);

        StringClientParam stringClientParam = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Patient.SP_ACTIVE).thenReturn(stringClientParam);
        StringClientParam.IStringMatch iStringMatch = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParam.matchesExactly()).thenReturn(iStringMatch);
        ICriterion iCriterion = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatch.value(anyString())).thenReturn(iCriterion);
        PowerMockito.when(iQuery.where(iCriterion)).thenReturn(iQuery);

        Bundle bundle = MockPatientUtil.givenAPatientBundle();
        PowerMockito.when(iQuery.preferResponseTypes(Arrays.asList(Patient.class, Practitioner.class))).thenReturn(iQuery);
        PowerMockito.when(iQuery.count(anyInt())).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        Pagination<Patient> patientPagination1 = fhirPatientInterface.queryPagingPatientList(patientQueryImmutablePairMap, 5, 1,1);
        Assert.assertThat(patientPagination1.getTotalCount(), is(2));
        Assert.assertThat(patientPagination1.getLstObject().size(), is(2));
    }
}