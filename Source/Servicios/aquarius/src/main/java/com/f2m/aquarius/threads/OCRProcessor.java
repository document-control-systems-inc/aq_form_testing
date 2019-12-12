package com.f2m.aquarius.threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.f2m.aquarius.parameters.BatchParams;
import com.f2m.aquarius.service.OCRService;
import com.f2m.aquarius.utils.AquariusException;

public class OCRProcessor implements Runnable {
	private final CountDownLatch countdownlatch;
	private OCRService ocr = new OCRService();

	public OCRProcessor(CountDownLatch countdownlatch) {
		this.countdownlatch = countdownlatch;
	}

	public void run() {
		System.out.println("Se ha iniciado la ejecución del OCRProcessor");
		try {
			while (!countdownlatch.await(BatchParams.threadOCRSleep, TimeUnit.SECONDS)) {
				if (BatchParams.threadOCRExecute) {
					try {
						ocr.processOCRByBatch();
					} catch (AquariusException e) {
						System.out.println("Error al procesar OCR: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Se ha interrumpido el proceso");
		}
		System.out.println("Se ha finalizado la ejecución de OCRProcessor");
	}
}
