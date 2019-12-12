package com.f2m.aquarius.beans;

import java.util.ArrayList;
import java.util.List;

public class User {
	private String uid;
	private String password;
	private String givenName;
	private String lastName;
	private String mail;
	private String dn;
	private String photo;
	private int sessionTime;
	private int maxConcurrent;
	private List<String> groups;
	private List<String> securityProfiles;
	private List<String> securityIdComponents;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDn() {
		return dn;
	}
	public void setDn(String dn) {
		this.dn = dn;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public int getSessionTime() {
		return sessionTime;
	}
	public void setSessionTime(int sessionTime) {
		this.sessionTime = sessionTime;
	}
	public int getMaxConcurrent() {
		return maxConcurrent;
	}
	public void setMaxConcurrent(int maxConcurrent) {
		this.maxConcurrent = maxConcurrent;
	}
	public List<String> getSecurityProfiles() {
		return securityProfiles;
	}
	public void setSecurityProfiles(List<String> securityProfiles) {
		this.securityProfiles = securityProfiles;
	}
	public List<String> getSecurityIdComponents() {
		return securityIdComponents;
	}
	public void setSecurityIdComponents(List<String> securityIdComponents) {
		this.securityIdComponents = securityIdComponents;
	}
	
	public void addGroup(String dn) {
		if (groups == null) {
			groups = new ArrayList<String>();
		}
		if (!groups.contains(dn)) {
			groups.add(dn);
		}
	}
	
	public Boolean removeGroup(String dn) {
		Boolean response = false;
		if (groups != null) {
			int indexOf = groups.indexOf(dn);
			if (indexOf > -1) {
				groups.remove(indexOf);
				response = true;
			}
		}
		return response;
	}
	
	public void addSecurityProfile(String profile) {
		if (securityProfiles == null) {
			securityProfiles = new ArrayList<String>();
		}
		if (!securityProfiles.contains(profile)) {
			securityProfiles.add(profile);
		}
	}
	
	public Boolean removeSecurityProfile(String profile) {
		Boolean response = false;
		if (securityProfiles != null) {
			int indexOf = securityProfiles.indexOf(profile);
			if (indexOf > -1) {
				securityProfiles.remove(indexOf);
				response = true;
			}
		}
		return response;
	}
	
	public void addSecurityIdComponent(String idComponent) {
		if (securityIdComponents == null) {
			securityIdComponents = new ArrayList<String>();
		}
		if (!securityIdComponents.contains(idComponent)) {
			securityIdComponents.add(idComponent);
		}
	}
	
	public Boolean removeSecurityIdComponent(String idComponent) {
		Boolean response = false;
		if (securityIdComponents != null) {
			int indexOf = securityIdComponents.indexOf(idComponent);
			if (indexOf > -1) {
				securityIdComponents.remove(indexOf);
				response = true;
			}
		}
		return response;
	}

	public String toString() {
		String response = "DN:" + getDn() + "\tUid:" + getUid() + "\tNombre:" 
				+ getGivenName() + "\tApellido:" + getLastName() + "\tEmail:" + getMail()
				+ "\tSessionTime:" + getSessionTime() + "\tMaxConcurrent:" + getMaxConcurrent();
		if (getGroups() != null) {
			response += "\tGrupos:";
			for (String group:getGroups()) {
				response += "\t" + group + "|";
			}
		}
		if (getSecurityProfiles() != null) {
			response += "\tPerfiles:";
			for (String securityProfile:getSecurityProfiles()) {
				response += "\t" + securityProfile + "|";
			}
		}
		if (getSecurityIdComponents() != null) {
			response += "\tComponentes:";
			for (String idElement:getSecurityIdComponents()) {
				response += "\t" + idElement + "|";
			}
		}
		return response;
	}
}
