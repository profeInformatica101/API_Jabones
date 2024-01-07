package com.dwes.api.entidades;

import jakarta.persistence.Embeddable;

@Embeddable
public class Ingrediente {
	
    private String elemento;
    private String cantidad; // Puede ser un String para permitir unidades (ej. "2 l", "400 g")
	
    public String getElemento() {
		return elemento;
	}
	public void setElemento(String elemento) {
		this.elemento = elemento;
	}
	public String getCantidad() {
		return cantidad;
	}
	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}
    
}
