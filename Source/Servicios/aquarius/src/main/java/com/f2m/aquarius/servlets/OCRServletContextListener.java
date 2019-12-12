package com.f2m.aquarius.servlets;

import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.f2m.aquarius.threads.OCRProcessor;

public class OCRServletContextListener implements ServletContextListener {

	
	private Thread thread = null;
	private CountDownLatch countdownLatch = null;
	
	
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Iniciando OCR");
		countdownLatch = new CountDownLatch(1);
        thread = new Thread(new OCRProcessor(countdownLatch));
        thread.setDaemon(true);
        thread.start();
        
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		if (countdownLatch != null) 
        {
            countdownLatch.countDown();
        }
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				System.out.println("Se ha interrumpido el thread");
			}
		}
		System.out.println("Finalizndo OCR");
    }
}
