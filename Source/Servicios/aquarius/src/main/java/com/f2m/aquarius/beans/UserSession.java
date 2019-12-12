package com.f2m.aquarius.beans;

import java.util.Date;

public class UserSession {
	
	private String userUid;
	private String sessionId;
	private Date connectedTime;
	private Date lastTransactionTime;
	private String hostnameConnected;
	
	public String getUserUid() {
		return userUid;
	}
	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}
	public Date getConnectedTime() {
		return connectedTime;
	}
	public void setConnectedTime(Date connectedTime) {
		this.connectedTime = connectedTime;
	}
	public Date getLastTransactionTime() {
		return lastTransactionTime;
	}
	public void setLastTransactionTime(Date lastTransactionTime) {
		this.lastTransactionTime = lastTransactionTime;
	}
	public String getHostnameConnected() {
		return hostnameConnected;
	}
	public void setHostnameConnected(String hostnameConnected) {
		this.hostnameConnected = hostnameConnected;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
		    return true;
		}
		if (obj == null) {
		    return false;
		}
		if (obj instanceof UserSession) {
			UserSession other = (UserSession)obj;
			boolean aux = false;
			if (other.getUserUid() != null && getUserUid() != null) {
				aux = other.getUserUid().equals(getUserUid());
			} else if (other.getSessionId() != null && getSessionId() != null) {
				aux = other.getSessionId().equals(getSessionId());
			}
		    return aux;
		
		} else {
		    return false;
		}
	}
}