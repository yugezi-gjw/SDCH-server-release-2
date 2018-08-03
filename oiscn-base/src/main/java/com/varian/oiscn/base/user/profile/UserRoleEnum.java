/**
 * 
 */
package com.varian.oiscn.base.user.profile;

/**
 * User Property Enumeration.<br>
 */
public enum UserRoleEnum {
	LOGIN_USER(1),
	PATIENT(2),
	OTHERS(99);
	
	private int userRoleNum;
	private UserRoleEnum(int num) {
		this.userRoleNum = num;
	}
	public int getValue() {
		return userRoleNum;
	}
}
