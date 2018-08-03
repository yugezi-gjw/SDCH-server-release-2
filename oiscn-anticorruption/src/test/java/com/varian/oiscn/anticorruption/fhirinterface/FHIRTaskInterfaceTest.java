package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.converter.EnumTaskQuery;
import com.varian.oiscn.anticorruption.datahelper.MockTaskUtil;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

/**
 * Created by fmk9441 on 2017-02-17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRTaskInterface.class, FHIRContextFactory.class})
public class FHIRTaskInterfaceTest {
    private static final String TASK_ID = "TaskId";
    private IGenericClient client;
    private FHIRContextFactory factory;
    private FHIRTaskInterface fhirTaskInterface;

    @Before
    public void setup() {
        client = PowerMockito.mock(IGenericClient.class);
        factory = PowerMockito.mock(FHIRContextFactory.getInstance().getClass());
        fhirTaskInterface = new FHIRTaskInterface();

        PowerMockito.mockStatic(FHIRContextFactory.class);
        PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(factory);
        PowerMockito.when(factory.newRestfulGenericClient()).thenReturn(client);
    }

    @Test
    public void givenATaskWhenCreateThenReturnTaskId() {
        Task task = MockTaskUtil.givenATask();
        ICreate iCreate = PowerMockito.mock(ICreate.class);
        PowerMockito.when(client.create()).thenReturn(iCreate);
        ICreateTyped iCreateTyped = PowerMockito.mock(ICreateTyped.class);
        PowerMockito.when(iCreate.resource(task)).thenReturn(iCreateTyped);
        MethodOutcome outcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iCreateTyped.execute()).thenReturn(outcome);

        IIdType iIdType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(outcome.getId()).thenReturn(iIdType);
        PowerMockito.when(iIdType.getIdPart()).thenReturn(TASK_ID);

        String createdTaskId = fhirTaskInterface.create(task);
        Assert.assertEquals(TASK_ID, createdTaskId);
    }

    @Test
    public void givenATaskWhenCreateThenThrowException() {
        Task task = MockTaskUtil.givenATask();
        PowerMockito.when(client.create()).thenThrow(Exception.class);
        String createdTaskId = fhirTaskInterface.create(task);
        Assert.assertTrue(StringUtils.isBlank(createdTaskId));
    }

    @Test
    public void givenATaskWhenUpdateThenReturnTaskId() {
        Task task = MockTaskUtil.givenATask();
        IUpdate iUpdate = PowerMockito.mock(IUpdate.class);
        PowerMockito.when(client.update()).thenReturn(iUpdate);
        IUpdateTyped iUpdateTyped = PowerMockito.mock(IUpdateTyped.class);
        PowerMockito.when(iUpdate.resource(task)).thenReturn(iUpdateTyped);
        MethodOutcome outcome = PowerMockito.mock(MethodOutcome.class);
        PowerMockito.when(iUpdateTyped.execute()).thenReturn(outcome);
        IIdType iIdType = PowerMockito.mock(IIdType.class);
        PowerMockito.when(outcome.getId()).thenReturn(iIdType);
        PowerMockito.when(iIdType.getIdPart()).thenReturn(TASK_ID);

        String updatedTaskId = fhirTaskInterface.update(task);
        Assert.assertEquals(TASK_ID, updatedTaskId);
    }

    @Test
    public void givenATaskWhenUpdateThenThrowException() {
        Task task = MockTaskUtil.givenATask();
        PowerMockito.when(client.update()).thenThrow(Exception.class);
        String updatedTaskId = fhirTaskInterface.update(task);
        Assert.assertTrue(StringUtils.isBlank(updatedTaskId));
    }

    @Test
    public void givenATaskIdWhenQueryThenReturnTask() {
        Task taskMock = MockTaskUtil.givenATask();

        IRead iRead = PowerMockito.mock(IRead.class);
        PowerMockito.when(client.read()).thenReturn(iRead);
        IReadTyped iReadTyped = PowerMockito.mock(IReadTyped.class);
        PowerMockito.when(iRead.resource(Task.class)).thenReturn(iReadTyped);
        IReadExecutable iReadExecutable = PowerMockito.mock(IReadExecutable.class);
        PowerMockito.when(iReadTyped.withId(TASK_ID)).thenReturn(iReadExecutable);
        PowerMockito.when(iReadExecutable.execute()).thenReturn(taskMock);

        Task taskReal = fhirTaskInterface.queryById(TASK_ID,Task.class);
        Assert.assertEquals(taskReal, taskMock);
    }

    @Test
    public void givenATaskIdWhenQueryThenThrowResourceNotFoundException() {
        PowerMockito.when(client.read()).thenThrow(ResourceNotFoundException.class);
        Task task = fhirTaskInterface.queryById(TASK_ID,Task.class);
        Assert.assertNull(task);
    }

    @Test
    public void givenATaskIdWhenQueryThenThrowException() {
        PowerMockito.when(client.read()).thenThrow(Exception.class);
        Task task = fhirTaskInterface.queryById(TASK_ID,Task.class);
        Assert.assertNull(task);
    }

    @Test
    public void givenANullMapWhenQueryThenReturnEmptyTaskList() {
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = null;
        List<Task> lstTask = fhirTaskInterface.queryTaskList(taskQueryImmutablePairMap);
        Assert.assertTrue(lstTask.isEmpty());
    }

    @Test
    public void givenAnEmptyLinkedHashMapWhenQueryThenReturnEmptyTaskList() {
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        List<Task> lstTask = fhirTaskInterface.queryTaskList(taskQueryImmutablePairMap);
        Assert.assertTrue(lstTask.isEmpty());
    }

    @Test
    public void givenAMapWithPatientIDAndStatusWhenQueryThenReturnTaskList() throws Exception {
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "PatientID"));
        taskQueryImmutablePairMap.put(EnumTaskQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Status"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Task.class)).thenReturn(iQuery);

        StringClientParam stringClientParamPatientID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Task.SP_PATIENT).thenReturn(stringClientParamPatientID);
        StringClientParam.IStringMatch iStringMatchPatientID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamPatientID.matchesExactly()).thenReturn(iStringMatchPatientID);
        ICriterion iCriterionPatientID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchPatientID.value(anyString())).thenReturn(iCriterionPatientID);
        PowerMockito.when(iQuery.where(iCriterionPatientID)).thenReturn(iQuery);

        StringClientParam stringClientParamStatus = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Task.SP_STATUS).thenReturn(stringClientParamStatus);
        StringClientParam.IStringMatch iStringMatchStatus = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamStatus.matchesExactly()).thenReturn(iStringMatchStatus);
        ICriterion iCriterionStatus = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchStatus.value(anyString())).thenReturn(iCriterionStatus);
        PowerMockito.when(iQuery.and(iCriterionStatus)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockTaskUtil.givenATaskBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Task> lstTask = fhirTaskInterface.queryTaskList(taskQueryImmutablePairMap);
        Assert.assertThat(1, is(lstTask.size()));
    }

    @Test
    public void givenAMapWithReasonAndBizStatusWhenQueryThenReturnTaskList() throws Exception {
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Reason"));
        taskQueryImmutablePairMap.put(EnumTaskQuery.BUSINESS_STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "BizStatus"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Task.class)).thenReturn(iQuery);

        StringClientParam stringClientParamReason = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Task.SP_REASON).thenReturn(stringClientParamReason);
        StringClientParam.IStringMatch iStringMatchReason = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamReason.matchesExactly()).thenReturn(iStringMatchReason);
        ICriterion iCriterionReason = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchReason.value(anyString())).thenReturn(iCriterionReason);
        PowerMockito.when(iQuery.where(iCriterionReason)).thenReturn(iQuery);

        StringClientParam stringClientParamBizStatus = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Task.SP_BUSINESS_STATUS).thenReturn(stringClientParamBizStatus);
        StringClientParam.IStringMatch iStringMatchBizStatus = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamBizStatus.matchesExactly()).thenReturn(iStringMatchBizStatus);
        ICriterion iCriterionBizStatus = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchBizStatus.value(anyString())).thenReturn(iCriterionBizStatus);
        PowerMockito.when(iQuery.and(iCriterionBizStatus)).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockTaskUtil.givenATaskBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Task> lstTask = fhirTaskInterface.queryTaskList(taskQueryImmutablePairMap);
        Assert.assertThat(1, is(lstTask.size()));
    }

    @Test
    public void givenAMapWithGroupIDAndReasonAndSortingWhenQueryThenReturnTaskList() throws Exception {
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.GROUP_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList("GroupID")));
        taskQueryImmutablePairMap.put(EnumTaskQuery.REASON_CODE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Reason"));
        taskQueryImmutablePairMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.ASC, "TaskDate"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Task.class)).thenReturn(iQuery);

        StringClientParam stringClientParamGroupID = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Task.SP_GROUP).thenReturn(stringClientParamGroupID);
        StringClientParam.IStringMatch iStringMatchGroupID = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamGroupID.matchesExactly()).thenReturn(iStringMatchGroupID);
        ICriterion iCriterionGroupID = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchGroupID.values(Arrays.asList(anyString()))).thenReturn(iCriterionGroupID);
        PowerMockito.when(iQuery.where(iCriterionGroupID)).thenReturn(iQuery);

        StringClientParam stringClientParamReason = PowerMockito.mock(StringClientParam.class);
        PowerMockito.whenNew(StringClientParam.class).withArguments(Task.SP_REASON).thenReturn(stringClientParamReason);
        StringClientParam.IStringMatch iStringMatchReason = PowerMockito.mock(StringClientParam.IStringMatch.class);
        PowerMockito.when(stringClientParamReason.matchesExactly()).thenReturn(iStringMatchReason);
        ICriterion iCriterionReason = PowerMockito.mock(ICriterion.class);
        PowerMockito.when(iStringMatchReason.value(anyString())).thenReturn(iCriterionReason);
        PowerMockito.when(iQuery.and(iCriterionReason)).thenReturn(iQuery);

        ISort iSort = PowerMockito.mock(ISort.class);
        PowerMockito.when(iQuery.sort()).thenReturn(iSort);
        PowerMockito.when(iSort.ascending(anyString())).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockTaskUtil.givenATaskBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Task> lstTask = fhirTaskInterface.queryTaskList(taskQueryImmutablePairMap);
        Assert.assertThat(1, is(lstTask.size()));
    }

    @Test
    public void givenAMapWithDataRangeAndSortingWhenQueryThenReturnTaskList() throws Exception {
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Start"));
        taskQueryImmutablePairMap.put(EnumTaskQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "End"));
        taskQueryImmutablePairMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "TaskDate"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Task.class)).thenReturn(iQuery);

        DateClientParam dateClientParam = PowerMockito.mock(DateClientParam.class);
        PowerMockito.whenNew(DateClientParam.class).withArguments(Task.SP_DATE_RANGE).thenReturn(dateClientParam);

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

        ISort iSort = PowerMockito.mock(ISort.class);
        PowerMockito.when(iQuery.sort()).thenReturn(iSort);
        PowerMockito.when(iSort.descending(anyString())).thenReturn(iQuery);

        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockTaskUtil.givenATaskBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        List<Task> lstTask = fhirTaskInterface.queryTaskList(taskQueryImmutablePairMap);
        Assert.assertThat(1, is(lstTask.size()));
    }

    @Test
    public void givenAMapWithDataRangeAndSortingAndPaginationWhenQueryThenReturnTaskList() throws Exception {
        Map<EnumTaskQuery, ImmutablePair<EnumMatchQuery, Object>> taskQueryImmutablePairMap = new LinkedHashMap<>();
        taskQueryImmutablePairMap.put(EnumTaskQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "Start"));
        taskQueryImmutablePairMap.put(EnumTaskQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, "End"));
        taskQueryImmutablePairMap.put(EnumTaskQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.DESC, "TaskDate"));

        IUntypedQuery iUntypedQuery = PowerMockito.mock(IUntypedQuery.class);
        PowerMockito.when(client.search()).thenReturn(iUntypedQuery);

        IQuery iQuery = PowerMockito.mock(IQuery.class);
        PowerMockito.when(iUntypedQuery.forResource(Task.class)).thenReturn(iQuery);

        DateClientParam dateClientParam = PowerMockito.mock(DateClientParam.class);
        PowerMockito.whenNew(DateClientParam.class).withArguments(Task.SP_DATE_RANGE).thenReturn(dateClientParam);

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

        ISort iSort = PowerMockito.mock(ISort.class);
        PowerMockito.when(iQuery.sort()).thenReturn(iSort);
        PowerMockito.when(iSort.descending(anyString())).thenReturn(iQuery);

        PowerMockito.when(iQuery.count(anyInt())).thenReturn(iQuery);
        PowerMockito.when(iQuery.returnBundle(Bundle.class)).thenReturn(iQuery);

        Bundle bundle = MockTaskUtil.givenATaskBundle();
        PowerMockito.when(iQuery.execute()).thenReturn(bundle);

        Pagination<Task> taskPagination = fhirTaskInterface.queryPagingTaskList(taskQueryImmutablePairMap, 5, 1,1);
        Assert.assertThat(5, is(taskPagination.getTotalCount()));
        Assert.assertThat(1, is(taskPagination.getLstObject().size()));
    }
}