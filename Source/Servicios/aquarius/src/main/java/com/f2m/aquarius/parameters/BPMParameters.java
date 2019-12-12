package com.f2m.aquarius.parameters;

public class BPMParameters {

	public static final String WF_STATUS_INITIAL = "0";
	public static final String WF_STATUS_IN_PROGRESS = "1";
	public static final String WF_STATUS_FINALIZED = "2";
	public static final String WF_STATUS_CANCELLED = "3";
	
	public static final String WF_STATUS_TEXT_INITIAL = "Creado";
	public static final String WF_STATUS_TEXT_IN_PROGRESS = "En progreso";
	public static final String WF_STATUS_TEXT_FINALIZED = "Finalizado";
	public static final String WF_STATUS_TEXT_CANCELLED = "Cancelado";
	
	public static final String TASK_STATUS_INITIAL = "0";
	public static final String TASK_STATUS_IN_PROGRESS = "1";
	public static final String TASK_STATUS_PAUSED = "2";
	public static final String TASK_STATUS_FINALIZED = "3";
	public static final String TASK_STATUS_CANCELLED = "4";
	
	public static final String TASK_STATUS_TEXT_INITIAL = "Creado";
	public static final String TASK_STATUS_TEXT_IN_PROGRESS = "En progreso";
	public static final String TASK_STATUS_TEXT_PAUSED = "Pospuesta";
	public static final String TASK_STATUS_TEXT_FINALIZED = "Finalizada";
	public static final String TASK_STATUS_TEXT_CANCELLED = "Cancelada";
	
	
	public static String getTaskStatus(String status) {
		switch(status) {
			case TASK_STATUS_INITIAL:
				return TASK_STATUS_TEXT_INITIAL;
			case TASK_STATUS_IN_PROGRESS:
				return TASK_STATUS_TEXT_IN_PROGRESS;
			case TASK_STATUS_PAUSED:
				return TASK_STATUS_TEXT_PAUSED;
			case TASK_STATUS_FINALIZED:
				return TASK_STATUS_TEXT_FINALIZED;
			case TASK_STATUS_CANCELLED:
				return TASK_STATUS_TEXT_CANCELLED;
		}
		return null;
	}
}
