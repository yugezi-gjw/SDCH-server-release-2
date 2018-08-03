/**
 * 
 */
package com.varian.oiscn.anticorruption.fhircontext;

import ca.uhn.fhir.rest.client.apache.ApacheHttpRequest;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import com.google.common.base.Splitter;
import com.varian.oiscn.core.hipaa.queue.AuditLogQueue;
import com.varian.oiscn.util.hipaa.HipaaEvent;
import com.varian.oiscn.util.hipaa.HipaaLogMessage;
import com.varian.oiscn.util.hipaa.HipaaObjectType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * FHIR Hipaa Logging Interceptor.<br>
 *
 */
@Slf4j
public class FHIRLoggingInterceptor implements IClientInterceptor {

	private static final String N_A = "[N/A]";

	@Override
	public void interceptRequest(IHttpRequest theRequest) {
		// all fhir request is GET request.
		ApacheHttpRequest apacheHttpRequest = (ApacheHttpRequest) theRequest;
        final String method = apacheHttpRequest.getHttpVerbName();
        
        HipaaLogMessage hipaaMsg = null;
        long nanoFilterStart = System.nanoTime();
		switch (method) {
		case "GET":
			hipaaMsg = handleGetRequest(apacheHttpRequest);
			break;
		default:
			// no need handle others
		}
		
		if (hipaaMsg != null) {
			log.debug(hipaaMsg.toString());
			long nanoHipaaStart = System.nanoTime();
			try {
				AuditLogQueue.getInstance().push(hipaaMsg);
			} catch (Throwable th) {
				log.error("AuditLogQueue Throwable: {}", th.getMessage());
			}
			log.debug("AuditLogQueue {} ms", (System.nanoTime() - nanoHipaaStart) * 1.0 / 1E6);
		}
		log.debug("Hipaa Request handle {} ms", (System.nanoTime() - nanoFilterStart) * 1.0 / 1E6);
	}

	@Override
	public void interceptResponse(IHttpResponse theResponse) throws IOException {

	}

	protected HipaaLogMessage handleGetRequest(ApacheHttpRequest httpRequest) {
		String userId = "";
		String objectId = N_A;
		String comment = N_A;
		String patientId = N_A;
		String queryParam = httpRequest.getApacheRequest().getURI().getQuery();
		if (StringUtils.isNotBlank(queryParam)) {
			List<String> list = Splitter.onPattern("&").splitToList(queryParam);
			for (int i = 0; i < list.size(); i++) {
				String line = list.get(i);
				if (line.startsWith("patient")) {
					List<String> values = Splitter.onPattern("=").splitToList(line);
					if (values.size() > 1) {
						patientId = values.get(1);
					}
				}
				if (line.contains("exact=")) {
					List<String> values = Splitter.onPattern("=").splitToList(line);
					if (values.size() > 1) {
						objectId = values.get(1);
					}
				} 
			}
			try {
				comment = URLDecoder.decode(queryParam, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		
		String prefix = getPathPrefix(httpRequest);
		
		HipaaLogMessage hippaMsg = new HipaaLogMessage();
		HipaaEvent event = HipaaEvent.Other;;
		HipaaObjectType objectType = HipaaObjectType.Other;
		switch (prefix) {
		// patient related
		case "Patient":
		case "Coverage":
		case "Flag":
		case "AllergyIntolerance":
		case "FamilyMemberHistory":
		case "Immunization":
			event = HipaaEvent.View;
			objectType = HipaaObjectType.Patient;
			break;
		// Eclipse || Treatment related
		case "ActivityDefinition":
		case "Task":
			event = HipaaEvent.View;
			objectType = HipaaObjectType.Activities;
			break;
		case "Appointment":
			event = HipaaEvent.View;
			objectType = HipaaObjectType.Appointment;
			break;
		case "Diagnosis":
			event = HipaaEvent.View;
			objectType = HipaaObjectType.Diagnosis;
			break;
		case "CarePath":
		case "RTPrescription":
		case "ProcedureRequest":
		case "TreatmentRecordHistory":
		case "TreatmentSummary":
			event = HipaaEvent.View;
			objectType = HipaaObjectType.Plan;
			break;
		default:
			event = HipaaEvent.Other;
			objectType = HipaaObjectType.Other;
		}
		
		hippaMsg.setUserId(userId);
		hippaMsg.setPatientId(patientId);
		hippaMsg.setEvent(event);
		hippaMsg.setObjectType(objectType);
		hippaMsg.setObjectId(objectId);
		hippaMsg.setComment(comment);
		return hippaMsg;
	}

	protected String getPathPrefix(ApacheHttpRequest httpRequest) {
		String pathInfo = httpRequest.getApacheRequest().getURI().getPath();
		if (pathInfo.length() > 5) {
		int endIndex = pathInfo.indexOf("/", 6);
			return endIndex >= 0 ? pathInfo.substring(6, endIndex): pathInfo.substring(6);
		}
		// /fhir?xxx=yyy
		return "/";
	}
}
