package com.varian.oiscn.base.user.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Property Object for API.<br>
 *
 */
@AllArgsConstructor
@Data
public class PropertyVO {
	/** property name */
	protected String property;
	/** property value */
	protected String value;
}
