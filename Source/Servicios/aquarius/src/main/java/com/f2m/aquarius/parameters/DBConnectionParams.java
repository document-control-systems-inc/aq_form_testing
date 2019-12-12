package com.f2m.aquarius.parameters;

public class DBConnectionParams {

	private String hostname;
	private String port;
	private String dbName;
	private String userName;
	private String password;
	private int validationInterval;
	private int timeBetweenEvictionRuns;
	private int maxActive;
	private int initialSize;
	private int maxWait;
	private int removeAbandonedTimeout;
	private int minEvictableIdleTime;
	private int minIdle;
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getValidationInterval() {
		return validationInterval;
	}
	public void setValidationInterval(int validationInterval) {
		this.validationInterval = validationInterval;
	}
	public int getTimeBetweenEvictionRuns() {
		return timeBetweenEvictionRuns;
	}
	public void setTimeBetweenEvictionRuns(int timeBetweenEvictionRuns) {
		this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
	}
	public int getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	public int getInitialSize() {
		return initialSize;
	}
	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}
	public int getMaxWait() {
		return maxWait;
	}
	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}
	public int getRemoveAbandonedTimeout() {
		return removeAbandonedTimeout;
	}
	public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
		this.removeAbandonedTimeout = removeAbandonedTimeout;
	}
	public int getMinEvictableIdleTime() {
		return minEvictableIdleTime;
	}
	public void setMinEvictableIdleTime(int minEvictableIdleTime) {
		this.minEvictableIdleTime = minEvictableIdleTime;
	}
	public int getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	
	
}
