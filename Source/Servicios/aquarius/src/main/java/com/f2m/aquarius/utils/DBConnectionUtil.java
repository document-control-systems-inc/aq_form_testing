package com.f2m.aquarius.utils;

import java.sql.Connection;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.f2m.aquarius.parameters.DBConnectionParams;

public class DBConnectionUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(DBConnectionUtil.class);
			
	private static DataSource datasource = null;
	
	private static DataSource getDataSource(DBConnectionParams params) {
		if (datasource == null) {
			PoolProperties p = new PoolProperties();
			p.setUrl("jdbc:postgresql://" + params.getHostname() + ":" + params.getPort() + "/" + params.getDbName());
			p.setDriverClassName("org.postgresql.Driver");
			p.setUsername(params.getUserName());
			p.setPassword(params.getPassword());
			p.setJmxEnabled(true);
			p.setTestWhileIdle(false);
			p.setTestOnBorrow(true);
			p.setValidationQuery("SELECT 1");
			p.setTestOnReturn(false);
			p.setValidationInterval(params.getValidationInterval());
			p.setTimeBetweenEvictionRunsMillis(params.getTimeBetweenEvictionRuns());
			p.setMaxActive(params.getMaxActive());
			p.setInitialSize(params.getInitialSize());
			p.setMaxWait(params.getMaxWait());
			p.setRemoveAbandonedTimeout(params.getRemoveAbandonedTimeout());
			p.setMinEvictableIdleTimeMillis(params.getMinEvictableIdleTime());
			p.setMinIdle(10);
			p.setLogAbandoned(true);
			p.setRemoveAbandoned(true);
			p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
			   "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
			datasource = new DataSource();
	        datasource.setPoolProperties(p);
		}
		return datasource;
	}
	
	public static Connection getConnection(DBConnectionParams params) throws AquariusException {
		GeneralUtils gutils = new GeneralUtils();
		Connection con = null;
		try {
			con = getDataSource(params).getConnection();
		} catch (Exception e) {
			logger.error("Error de conexión con la Base de Datos: " + e.getMessage());
			throw gutils.throwException(501, e.getMessage());
		}
		return con;
	}	
}
