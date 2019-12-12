package com.f2m.aquarius.beans;

public class ECMFile {
	private String id;
	private String nombre;
	
	public String toString() {
		return "Archivo: " + getNombre() + "\tId: " + getId();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
