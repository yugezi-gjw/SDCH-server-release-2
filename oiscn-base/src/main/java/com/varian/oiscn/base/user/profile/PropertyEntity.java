package com.varian.oiscn.base.user.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PropertyEntity {
	/** property Item */
	protected String property;
	/** property value */
	protected String value;
}
