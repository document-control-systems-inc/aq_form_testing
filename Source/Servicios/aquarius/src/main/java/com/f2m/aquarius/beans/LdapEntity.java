package com.f2m.aquarius.beans;

public class LdapEntity {

	private String dn;
	private String objectClass;
	
	public String getDn() {
		return dn;
	}
	public void setDn(String dn) {
		this.dn = dn;
	}
	public String getObjectClass() {
		return objectClass;
	}
	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}
	
	public String toString() {
		return "DN:\t" + getDn() + "\tObjectClass:\t" + getObjectClass();
	}
}
