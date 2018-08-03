/**
 * 
 */
package com.varian.oiscn.base.user.profile;

/**
 * User Property Enumeration.<br>
 *
 */
public enum PropertyEnum {
	SHOW_DONE_HINT("ShowDoneHint", "true"),
	AUTO_LOGOUT("AutoLogout", "true"), 
	;
	private String name;
	private String defaultValue;

	private PropertyEnum(String propertyName, String defaultValue) {
		this.name = propertyName;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return this.name;
	}
	
	public String getDefaultValue() {
		return this.defaultValue;
	}
}
