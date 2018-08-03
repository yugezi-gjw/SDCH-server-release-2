package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.varian.fhir.resources.Appointment;
import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.converter.EnumAppointmentQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.datahelper.MockAppointmentUtil;
import com.varian.oiscn.anticorruption.datahelper.MockTaskUtil;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.appointment.AppointmentRankEnum;
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
 * Created by fmk9441 on 2017-02-14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRAppointmentInterface.class, FHIRContextFactory.class})
public class FHIRAppointmentInterfaceTest {
    private static final String APPOINTMENT_ID = "AppointmentId";
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRAppointmentInterface fhirAppointmentInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirAppointmentInterface = new FHIRAppointmentInterface();

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void givenAnAppointmentDtoWhenCreateThenReturnAppointmentId() {
        Appointment appointment = PowerMockito.mock(Appointment.class);

        ICreate iCreate = PowerMockito.mock(ICreate.class);
        PowerMockito.when(client.create()).thenReturn(iCreate);
        ICreateTyped iCreateTyped = PowerMockito.mock(ICreateTyped.class);
        PowerMockito.when(iCreate.resource(appointment)).thenReturn(iCreateTyped);

        MethodOutcome methodOutcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iCreateTyped.execute()).thenReturn(methodOutcome);

        IIdType iIdType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(methodOutcome.getId()).thenReturn(iIdType);
        PowerMockito.when(iIdType.getIdPart()).thenReturn(APPOINTMENT_ID);

        String createdAppointmentId = fhirAppointmentInterface.create(appointment);

        Assert.assertEquals(APPOINTMENT_ID, createdAppointmentId);
    }

    @Test
    public void givenAnAppointmentDtoWhenCreateThenThrowException() {
        Appointment appointment = MockAppointmentUtil.givenAnAppointment();
        PowerMockito.when(client.create()).thenThrow(Exception.class);
        String createdAppointmentId = fhirAppointmentInterface.create(appointment);
        Assert.assertTrue(StringUtils.isBlank(createdAppointmentId));
    }

    @Test
    public void givenAnAppointmentDtoWhenUpdateThenReturnAppointmentId() {
        Appointment appointment = PowerMockito.mock(Appointment.class);
        IUpdate iUpdate = PowerMockito.mock(IUpdate.class);
        PowerMockito.when(client.update()).thenReturn(iUpdate);
        IUpdateTyped iUpdateTyped = PowerMockito.mock(IUpdateTyped.class);
        PowerMockito.when(iUpdate.resource(appointment)).thenReturn(iUpdateTyped);
        MethodOutcome methodOutcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iUpdateTyped.execute()).thenReturn(methodOutcome);

        IIdType iIdType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(methodOutcome.getId()).thenReturn(iIdType);
        PowerMockito.when(iIdType.getIdPart()).thenReturn(APPOINTMENT_ID);

        String updatedAppointmentId = fhirAppointmentInterface.update(appointment);

        Assert.assertEquals(APPOINTMENT_ID, updatedAppointmentId);
    }

    @Test
    public void givenAnAppointmentDtoWhenUpdateThenThrowException() {
        Appointment appointment = MockAppointmentUtil.givenAnAppointment();
        PowerMockito.when(client.update()).thenThrow(Exception.class);
        String updatedAppointmentId = fhirAppointmentInterface.update(appointment);
        Assert.assertTrue(StringUtils.isBlank(updatedAppointmentId));
    }

    @Test
    public void givenAnAppointmentIdWhenQueryThenReturnAppointment() {
        Appointment appointmentMock = MockAppointmentUtil.givenAnAppointment();

        IRead iRead = PowerMockito.mock(IRead.class);
        PowerMockito.when(client.read()).thenReturn(iRead);
        IReadTyped iReadTyped = PowerMockito.mock(IReadTyped.class);
        PowerMockito.when(iRead.resource(Appointment.class)).thenReturn(iReadTyped);
        IReadExecutable iReadExecutable = PowerMockito.mock(IReadExecutable.class);
        PowerMockito.when(iReadTyped.withId(anyString())).thenReturn(iReadExecutable);
        PowerMockito.when(iReadExecutable.execute()).thenReturn(appointmentMock);

        Appointment appointmentReal = fhirAppointmentInterface.queryById(APPOINTMENT_ID,Appointment.class);
        Assert.assertEquals(appointmentMock, appointmentReal);
    }

    @Test
    public void givenAnAppointmentIdWhenQueryThenThrowException() {
        PowerMockito.when(client.read()).thenThrow(Exception.class);

        Appointment appointment = fhirAppointmentInterface.queryById(APPOINTMENT_ID,Appointment.class);
        Assert.assertNull(appointment);
    }

    @Test
    public void givenAnAppointmentIdWhenQueryThenThrowAResourceNotFoundException() {
        PowerMockito.when(client.read()).thenThrow(ResourceNotFoundException.class);
        Appointment appointment = fhirAppointmentInterface.queryById(APPOINTMENT_ID,Appointment.class);
        Assert.assertNull(appointment);
    }

    @Test
    public void givenATaskIdWhenQueryThenReturnAppointmentList() {
        final String taskId = "TaskId";
        Task task = MockTaskUtil.givenATask();
        IRead iRead = PowerMockito.mock(IRead.class);
        PowerMockito.when(client.read()).thenReturn(iRead);
        IReadTyped iReadTypedTask = PowerMockito.mock(IReadTyped.class);
        PowerMockito.when(iRead.resource(Task.class)).thenReturn(iReadTypedTask);
        IReadExecutable iReadExecutableTask = PowerMockito.mock(IReadExecutable.class);
        PowerMockito.when(iReadTypedTask.withId(anyString())).thenReturn(iReadExecutableTask);
        PowerMockito.when(iReadExecutableTask.execute()).thenReturn(task);

        Appointment appointment = MockAppointmentUtil.givenAnAppointment();
        IReadTyped iReadTypedAppointment = PowerMockito.mock(IReadTyped.class);
        PowerMockito.when(iRead.resource(Appointment.class)).thenReturn(iReadTypedAppointment);
        IReadExecutable iReadExecutableAppointment = PowerMockito.mock(IReadExecutable.class);
        PowerMockito.when(iReadTypedAppointment.withId(anyString())).thenReturn(iReadExecutableAppointment);
        PowerMockito.when(iReadExecutableAppointment.execute()).thenReturn(appointment);

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentListByTaskId(taskId);

        Assert.assertThat(1, is(lstAppointment.size()));
    }

    @Test
    public void givenATaskIdWhenQueryThenThrowException() {
        final String taskId = "TaskId";
        PowerMockito.when(client.read()).thenThrow(Exception.class);
        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentListByTaskId(taskId);
        Assert.assertTrue(lstAppointment.isEmpty());
    }

    @Test
    public void givenANullMapWhenQueryThenThrowNullAppointmentList() {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = null;
        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        Assert.assertTrue(lstAppointment.isEmpty());
    }

    @Test
    public void givenAMapWithAppointmentIDAndStatusWhenQueryThenReturnAppointmentList() throws Exception {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "AppointmentID"));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("Status")));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Appointment.class)).thenReturn(iQuery);

        StringClientParam stringClientParamID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_RES_ID).thenReturn(stringClientParamID);
        StringClientParam.IStringMatch iStringMatchID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamID.matchesExactly()).thenReturn(iStringMatchID);
        ICriterion iCriterionID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchID.value(anyString())).thenReturn(iCriterionID);
        PowerMockito.when(iQuery.where(iCriterionID)).thenReturn(iQuery);

        StringClientParam stringClientParamStatus = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_STATUS).thenReturn(stringClientParamStatus);
        StringClientParam.IStringMatch iStringMatchStatus = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamStatus.matchesExactly()).thenReturn(iStringMatchStatus);
        ICriterion iCriterionStatus = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchStatus.values(anyList())).thenReturn(iCriterionStatus);
        PowerMockito.when(iQuery.and(iCriterionStatus)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockAppointmentUtil.givenAnAppointmentBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        Assert.assertThat(1, is(lstAppointment.size()));
    }

    @Test
    public void givenAMapWithStatusAndPatientIDWhenQueryThenReturnAppointmentList() throws Exception {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("Status")));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("PatientID1", "PatientID2")));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Appointment.class)).thenReturn(iQuery);

        StringClientParam stringClientParamStatus = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_STATUS).thenReturn(stringClientParamStatus);
        StringClientParam.IStringMatch iStringMatchStatus = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamStatus.matchesExactly()).thenReturn(iStringMatchStatus);
        ICriterion iCriterionStatus = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchStatus.values(anyList())).thenReturn(iCriterionStatus);
        PowerMockito.when(iQuery.where(iCriterionStatus)).thenReturn(iQuery);

        StringClientParam stringClientParamPatientID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_PATIENT).thenReturn(stringClientParamPatientID);
        StringClientParam.IStringMatch iStringMatchPatientID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamPatientID.matchesExactly()).thenReturn(iStringMatchPatientID);
        ICriterion iCriterionPatientID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchPatientID.value(anyString())).thenReturn(iCriterionPatientID);
        PowerMockito.when(iQuery.and(iCriterionPatientID)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockAppointmentUtil.givenAnAppointmentBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        Assert.assertThat(1, is(lstAppointment.size()));
    }

    @Test
    public void givenAMapWithPractitionerIDAndAppointmentReasonWhenQueryThenReturnAppointmentList() throws Exception {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PRACTITIONER_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("PractitionerID1", "PractitionerID2")));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "AppointmentReason"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Appointment.class)).thenReturn(iQuery);

        StringClientParam stringClientParamPractitionerID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_PRACTITIONER).thenReturn(stringClientParamPractitionerID);
        StringClientParam.IStringMatch iStringMatchPractitionerID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamPractitionerID.matchesExactly()).thenReturn(iStringMatchPractitionerID);
        ICriterion iCriterionPractitionerID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchPractitionerID.value(anyString())).thenReturn(iCriterionPractitionerID);
        PowerMockito.when(iQuery.where(iCriterionPractitionerID)).thenReturn(iQuery);

        StringClientParam stringClientParamReason = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments("AppointmentReason").thenReturn(stringClientParamReason);
        StringClientParam.IStringMatch iStringMatchReason = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamReason.matchesExactly()).thenReturn(iStringMatchReason);
        ICriterion iCriterionReason = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchReason.value(anyString())).thenReturn(iCriterionReason);
        PowerMockito.when(iQuery.and(iCriterionReason)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockAppointmentUtil.givenAnAppointmentBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        Assert.assertThat(1, is(lstAppointment.size()));
    }

    @Test
    public void givenAMapWithAppointmentReasonAndActorIDWhenQueryThenReturnAppointmentList() throws Exception {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "AppointmentReason"));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("ActorID1", "ActorID2")));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Appointment.class)).thenReturn(iQuery);

        StringClientParam stringClientParamReason = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments("AppointmentReason").thenReturn(stringClientParamReason);
        StringClientParam.IStringMatch iStringMatchReason = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamReason.matchesExactly()).thenReturn(iStringMatchReason);
        ICriterion iCriterionReason = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchReason.value(anyString())).thenReturn(iCriterionReason);
        PowerMockito.when(iQuery.where(iCriterionReason)).thenReturn(iQuery);

        StringClientParam stringClientParamActorID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_ACTOR).thenReturn(stringClientParamActorID);
        StringClientParam.IStringMatch iStringMatchActorID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamActorID.matchesExactly()).thenReturn(iStringMatchActorID);
        ICriterion iCriterionActorID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchActorID.values(Arrays.asList(anyString()))).thenReturn(iCriterionActorID);
        PowerMockito.when(iQuery.and(iCriterionActorID)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockAppointmentUtil.givenAnAppointmentBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        Assert.assertThat(1, is(lstAppointment.size()));
    }

    @Test
    public void givenAMapWithActorIDAndAppointmentDateAndSortingWhenQueryThenReturnAppointmentList() throws Exception {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("ActorID1", "ActorID2")));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "AppointmentDate"));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, AppointmentRankEnum.START_TIME));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Appointment.class)).thenReturn(iQuery);

        StringClientParam stringClientParamActorID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_ACTOR).thenReturn(stringClientParamActorID);
        StringClientParam.IStringMatch iStringMatchActorID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamActorID.matchesExactly()).thenReturn(iStringMatchActorID);
        ICriterion iCriterionActorID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchActorID.values(Arrays.asList(anyString()))).thenReturn(iCriterionActorID);
        PowerMockito.when(iQuery.where(iCriterionActorID)).thenReturn(iQuery);

        DateClientParam dateClientParam = PowerMockito.mock(DateClientParam.class);
        PowerMockito.whenNew(DateClientParam.class).withArguments(Appointment.SP_DATE).thenReturn(dateClientParam);
        DateClientParam.IDateSpecifier iDateSpecifier = PowerMockito.mock(DateClientParam.IDateSpecifier.class);
        PowerMockito.when(dateClientParam.exactly()).thenReturn(iDateSpecifier);
        DateClientParam.IDateCriterion iDateCriterion = PowerMockito.mock(DateClientParam.IDateCriterion.class);
        PowerMockito.when(iDateSpecifier.day(anyString())).thenReturn(iDateCriterion);
        PowerMockito.when(iQuery.and(iDateCriterion)).thenReturn(iQuery);

        ISort iSort = PowerMockito.mock(ISort.class);
        PowerMockito.when(iQuery.sort()).thenReturn(iSort);
        PowerMockito.when(iSort.descending(anyString())).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockAppointmentUtil.givenAnAppointmentBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        Assert.assertThat(1, is(lstAppointment.size()));
    }

    @Test
    public void givenAMapWithAppointmentDateAndPractitionerIDWhenQueryThenReturnAppointmentList() throws Exception {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "AppointmentDate"));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PRACTITIONER_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "PractitionerID"));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.ASC, AppointmentRankEnum.START_TIME));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Appointment.class)).thenReturn(iQuery);

        DateClientParam dateClientParam = PowerMockito.mock(DateClientParam.class);
        PowerMockito.whenNew(DateClientParam.class).withArguments(Appointment.SP_DATE).thenReturn(dateClientParam);
        DateClientParam.IDateSpecifier iDateSpecifier = PowerMockito.mock(DateClientParam.IDateSpecifier.class);
        PowerMockito.when(dateClientParam.exactly()).thenReturn(iDateSpecifier);
        DateClientParam.IDateCriterion iDateCriterion = PowerMockito.mock(DateClientParam.IDateCriterion.class);
        PowerMockito.when(iDateSpecifier.day(anyString())).thenReturn(iDateCriterion);

        PowerMockito.when(iQuery.where(iDateCriterion)).thenReturn(iQuery);

        StringClientParam stringClientParamPractitionerID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_PRACTITIONER).thenReturn(stringClientParamPractitionerID);
        StringClientParam.IStringMatch iStringMatchPractitionerID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamPractitionerID.matchesExactly()).thenReturn(iStringMatchPractitionerID);
        ICriterion iCriterionPractitionerID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchPractitionerID.value(anyString())).thenReturn(iCriterionPractitionerID);
        PowerMockito.when(iQuery.and(iCriterionPractitionerID)).thenReturn(iQuery);

        ISort iSort = PowerMockito.mock(ISort.class);
        PowerMockito.when(iQuery.sort()).thenReturn(iSort);
        PowerMockito.when(iSort.ascending(anyString())).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockAppointmentUtil.givenAnAppointmentBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        Assert.assertThat(1, is(lstAppointment.size()));
    }

    @Test
    public void givenAMapWithActorIDAndDateRangeWhenQueryThenReturnAppointmentList() throws Exception {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("ActorID")));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Start"));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "End"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Appointment.class)).thenReturn(iQuery);

        StringClientParam stringClientParamActorID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_ACTOR).thenReturn(stringClientParamActorID);
        StringClientParam.IStringMatch iStringMatchActorID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamActorID.matchesExactly()).thenReturn(iStringMatchActorID);
        ICriterion iCriterionActorID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchActorID.values(Arrays.asList(anyString()))).thenReturn(iCriterionActorID);
        PowerMockito.when(iQuery.where(iCriterionActorID)).thenReturn(iQuery);

        DateClientParam dateClientParam = PowerMockito.mock(DateClientParam.class);
        PowerMockito.whenNew(DateClientParam.class).withArguments(Appointment.SP_DATE_RANGE).thenReturn(dateClientParam);

        DateClientParam.IDateSpecifier iDateSpecifierAfter = PowerMockito.mock(DateClientParam.IDateSpecifier.class);
        PowerMockito.when(dateClientParam.afterOrEquals()).thenReturn(iDateSpecifierAfter);
        DateClientParam.IDateCriterion iDateCriterionAfter = PowerMockito.mock(DateClientParam.IDateCriterion.class);
        PowerMockito.when(iDateSpecifierAfter.day(anyString())).thenReturn(iDateCriterionAfter);
        PowerMockito.when(iQuery.and(iDateCriterionAfter)).thenReturn(iQuery);

        DateClientParam.IDateSpecifier iDateSpecifierBefore = PowerMockito.mock(DateClientParam.IDateSpecifier.class);
        PowerMockito.when(dateClientParam.beforeOrEquals()).thenReturn(iDateSpecifierBefore);
        DateClientParam.IDateCriterion iDateCriterionBefore = PowerMockito.mock(DateClientParam.IDateCriterion.class);
        PowerMockito.when(iDateSpecifierBefore.day(anyString())).thenReturn(iDateCriterionBefore);
        PowerMockito.when(iQuery.and(iDateCriterionBefore)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockAppointmentUtil.givenAnAppointmentBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        Assert.assertThat(1, is(lstAppointment.size()));
    }

    @Test
    public void givenAMapWithDateRangeAndPatientIDWhenQueryThenReturnAppointmentList() throws Exception {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Start"));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "End"));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "PatientID"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Appointment.class)).thenReturn(iQuery);

        DateClientParam dateClientParam = PowerMockito.mock(DateClientParam.class);
        PowerMockito.whenNew(DateClientParam.class).withArguments(Appointment.SP_DATE_RANGE).thenReturn(dateClientParam);

        DateClientParam.IDateSpecifier iDateSpecifierAfter = PowerMockito.mock(DateClientParam.IDateSpecifier.class);
        PowerMockito.when(dateClientParam.afterOrEquals()).thenReturn(iDateSpecifierAfter);
        DateClientParam.IDateCriterion iDateCriterionAfter = PowerMockito.mock(DateClientParam.IDateCriterion.class);
        PowerMockito.when(iDateSpecifierAfter.day(anyString())).thenReturn(iDateCriterionAfter);
        PowerMockito.when(iQuery.where(iDateCriterionAfter)).thenReturn(iQuery);

        DateClientParam.IDateSpecifier iDateSpecifierBefore = PowerMockito.mock(DateClientParam.IDateSpecifier.class);
        PowerMockito.when(dateClientParam.beforeOrEquals()).thenReturn(iDateSpecifierBefore);
        DateClientParam.IDateCriterion iDateCriterionBefore = PowerMockito.mock(DateClientParam.IDateCriterion.class);
        PowerMockito.when(iDateSpecifierBefore.day(anyString())).thenReturn(iDateCriterionBefore);
        PowerMockito.when(iQuery.and(iDateCriterionBefore)).thenReturn(iQuery);

        StringClientParam stringClientParamPatientID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_PATIENT).thenReturn(stringClientParamPatientID);
        StringClientParam.IStringMatch iStringMatchPatientID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamPatientID.matchesExactly()).thenReturn(iStringMatchPatientID);
        ICriterion iCriterionPatientID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchPatientID.value(anyString())).thenReturn(iCriterionPatientID);
        PowerMockito.when(iQuery.and(iCriterionPatientID)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockAppointmentUtil.givenAnAppointmentBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        Assert.assertThat(1, is(lstAppointment.size()));
    }

    @Test
    public void givenAMapWithAppointmentReasonAndActorIDWithPaginationWhenQueryThenReturnAppointmentList() throws Exception {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "AppointmentReason"));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("ActorID1", "ActorID2")));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Appointment.class)).thenReturn(iQuery);

        StringClientParam stringClientParamReason = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments("AppointmentReason").thenReturn(stringClientParamReason);
        StringClientParam.IStringMatch iStringMatchReason = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamReason.matchesExactly()).thenReturn(iStringMatchReason);
        ICriterion iCriterionReason = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchReason.value(anyString())).thenReturn(iCriterionReason);
        PowerMockito.when(iQuery.where(iCriterionReason)).thenReturn(iQuery);

        StringClientParam stringClientParamActorID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Appointment.SP_ACTOR).thenReturn(stringClientParamActorID);
        StringClientParam.IStringMatch iStringMatchActorID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamActorID.matchesExactly()).thenReturn(iStringMatchActorID);
        ICriterion iCriterionActorID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchActorID.values(Arrays.asList(anyString()))).thenReturn(iCriterionActorID);
        PowerMockito.when(iQuery.and(iCriterionActorID)).thenReturn(iQuery);
        PowerMockito.when(iQuery.count(anyInt())).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockAppointmentUtil.givenAnAppointmentBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        Pagination<Appointment> appointmentPagination = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, 5, 1, 1);
        Assert.assertThat(5, is(appointmentPagination.getTotalCount()));
        Assert.assertThat(1, is(appointmentPagination.getLstObject().size()));
    }
}