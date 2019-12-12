package com.f2m.aquarius.beans;

public class ECMFolder {
	
	private String id;
	private String nombre;
	private String ruta;
	
	public String toString() {
		return "Folder: " + getNombre() + "\tId: " + getId() + "\tRuta: " + getRuta();
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
	public String getRuta() {
		return ruta;
	}
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

}
