package com.f2m.aquarius.beans;

import java.util.ArrayList;
import java.util.List;

public class Group {
	private String dn;
	private String cn;
	private List<String> members;

	public String getCn() {
		return cn;
	}

	public void setCn(String name) {
		this.cn = name;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String id) {
		this.dn = id;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}
	
	public void addMember(String dn) {
		if (members == null) {
			members = new ArrayList<String>();
		}
		if (!members.contains(dn)) {
			members.add(dn);
		}
	}
	
	public Boolean removeMember(String dn) {
		Boolean response = false;
		if (members != null) {
			int indexOf = members.indexOf(dn);
			if (indexOf > -1) {
				members.remove(indexOf);
				response = true;
			}
		}
		return response;
	}
	
	public String toString() {
		String response = "DN:\t" + getDn() + "\tCN:\t" + getCn();
		if (getMembers() != null) {
			response += "\tMiembros:";
			for (String member:getMembers()) {
				response += "\t" + member + "|";
			}
		}
		return response;
	}
}
