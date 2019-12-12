package com.f2m.aquarius.services;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f2m.aquarius.utils.ServiceParams;
import com.fasterxml.jackson.databind.JsonNode;

public class MonitorMagicFolder implements Runnable {

	private final CountDownLatch countdownlatch;
	private final static Logger logger = Logger.getLogger(MonitorMagicFolder.class.getName());
	private AquariusServiceFile serviceFile = new AquariusServiceFile();
	
	public MonitorMagicFolder(CountDownLatch countdownlatch) {
		this.countdownlatch = countdownlatch;
	}
	
	public void run() {
		logger.log(Level.INFO, "Se ha iniciado la ejecución del MonitorMagicFolder");
		readConf();
		try {
			while (!countdownlatch.await(ServiceParams.threadServiceSleep, TimeUnit.SECONDS)) {
				readConf();
				if (ServiceParams.threadServiceExecute) {
					try {
						logger.log(Level.INFO, "Monitor ejecutado...");
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error al procesar MonitorMagicFolder: " + e.getMessage(), e);
					}
				} else {
					countdownlatch.countDown();
				}
			}
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Se ha interrumpido el proceso", e);
		}
		logger.log(Level.INFO, "Se ha finalizado la ejecución de MonitorMagicFolder");
	
	}
	
	private void readConf() {
		JsonNode jsonConf = null;
		try {
			jsonConf = serviceFile.readConfFile();
		} catch (Exception e) { }
		if (jsonConf == null) {
			jsonConf = serviceFile.defaultConf();
		}
		ServiceParams.threadServiceExecute = jsonConf.findValue("threadServiceExecute").asBoolean();
		ServiceParams.threadServiceSleep = jsonConf.findValue("threadServiceSleep").asLong();
	}
	
	public static void main(String[] args) {
		CountDownLatch countdownLatch = new CountDownLatch(1);
		Thread thread = new Thread(new MonitorMagicFolder(countdownLatch));
		//thread.setDaemon(true);
        thread.start();
	}
	
}
