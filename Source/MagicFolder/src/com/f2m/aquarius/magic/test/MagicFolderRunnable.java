package com.f2m.aquarius.magic.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class MagicFolderRunnable implements Runnable {

	private InputStream inputStream;
    private Consumer<String> consumer;
	
    public MagicFolderRunnable(InputStream inputStream, Consumer<String> consumer) {
    	this.inputStream = inputStream;
        this.consumer = consumer;
    }
    
	@Override
	public void run() {
		new BufferedReader(new InputStreamReader(inputStream)).lines()
        .forEach(consumer);
	}

}
