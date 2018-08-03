package com.varian.oiscn.encounter;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Patient Helper.<br>
 */
public class PatientEncounterHelper {
	protected static final Map<String,PatientEncounterCarePath> patientEncounterCarePathInstanceIdMap = new ConcurrentHashMap<>();
	protected static final Map<String,PatientEncounterEndPlan> patientEncounterEndPlanMap = new ConcurrentHashMap<>();
	protected static EncounterCarePathServiceImpl encounterCarePathServiceImpl = new EncounterCarePathServiceImpl(null);
	protected static EncounterEndPlanServiceImpl encounterEndPlanServiceImpl = new EncounterEndPlanServiceImpl();

	/**
	 * Add New PatientEncounterCarePath and Patient Serial to Cache.<br>
	 * @param patientEncounterCarePath
	 */
	public static void addToPatientSerEncounterCarePathCache(PatientEncounterCarePath patientEncounterCarePath) {
		if(patientEncounterCarePath != null) {
			patientEncounterCarePathInstanceIdMap.put(patientEncounterCarePath.getPatientSer(), patientEncounterCarePath);
		}
	}

	public static void addToPatientSerEncounterEndPlanCache(PatientEncounterEndPlan patientEncounterEndPlan){
		if(patientEncounterEndPlan != null){
			patientEncounterEndPlanMap.put(patientEncounterEndPlan.getPatientSer(),patientEncounterEndPlan);
		}
	}

	/**
	 *
	 * @param patientSer
	 * @return
	 */
	public static PatientEncounterCarePath getEncounterCarePathByPatientSer(String patientSer) {
		PatientEncounterCarePath patientEncounterCarePath = patientEncounterCarePathInstanceIdMap.get(patientSer);
		if(patientEncounterCarePath == null){
			patientEncounterCarePath = encounterCarePathServiceImpl.queryEncounterCarePathByPatientSer(patientSer);
			if (patientEncounterCarePath != null) {
				addToPatientSerEncounterCarePathCache(patientEncounterCarePath);
			}
		}
		return patientEncounterCarePath;
	}

	public static PatientEncounterEndPlan getEncounterEndPlanByPatientSer(String patientSer){
		PatientEncounterEndPlan patientEncounterEndPlan = patientEncounterEndPlanMap.get(patientSer);
		if(patientEncounterEndPlan == null){
			patientEncounterEndPlan = new PatientEncounterEndPlan(){{
				setPatientSer(patientSer);
				setCompletedPlan(encounterEndPlanServiceImpl.queryEncounterEndPlanListByPatientSer(patientSer));
			}};
			patientEncounterEndPlanMap.put(patientSer,patientEncounterEndPlan);
		}
		return patientEncounterEndPlan;
	}

	/**
	 *
	 * @param patientSer
	 */
    public static void syncEncounterCarePathByPatientSer(String patientSer) {
		if(StringUtils.isNotEmpty(patientSer)){
			PatientEncounterCarePath patientEncounterCarePath = encounterCarePathServiceImpl.queryEncounterCarePathByPatientSer(patientSer);
			if (patientEncounterCarePath != null) {
				addToPatientSerEncounterCarePathCache(patientEncounterCarePath);
			}
		}
	}

	public static void syncEncounterEndPlanByPatientSer(String patientSer){
		if(StringUtils.isNotEmpty(patientSer)){
			PatientEncounterEndPlan patientEncounterEndPlan = patientEncounterEndPlanMap.get(patientSer);
			patientEncounterEndPlan.setCompletedPlan(encounterEndPlanServiceImpl.queryEncounterEndPlanListByPatientSer(patientSer));
		}
	}
}
