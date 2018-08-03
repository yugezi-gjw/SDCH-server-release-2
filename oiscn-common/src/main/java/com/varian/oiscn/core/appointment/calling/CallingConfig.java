package com.varian.oiscn.core.appointment.calling;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 10/25/2017
 * @Modified By:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallingConfig {

	@JsonProperty
	private String logo;

	@JsonProperty
	private String companyInfo;

	@JsonProperty
	private SystemInfo systemInfo;

	@JsonProperty
	private CheckInGuide checkInGuide;

	@JsonProperty
	private CallingPatientGuide callingPatientGuide;


}
