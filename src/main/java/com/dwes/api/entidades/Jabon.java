package com.dwes.api.entidades;

import java.util.ArrayList;
import java.util.List;

import com.dwes.api.entidades.enumerados.TipoDePiel;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "jabones")
public class Jabon extends Producto {
    private String aroma;

    @Enumerated(EnumType.STRING)
    private TipoDePiel tipoDePiel;

    @ElementCollection
    @CollectionTable(name = "jabon_ingredientes", joinColumns = @JoinColumn(name = "jabon_id"))
    private List<Ingrediente> ingredientes = new ArrayList<>();

	public String getAroma() {
		return aroma;
	}

	public void setAroma(String aroma) {
		this.aroma = aroma;
	}

	public TipoDePiel getTipoDePiel() {
		return tipoDePiel;
	}

	public void setTipoDePiel(TipoDePiel tipoDePiel) {
		this.tipoDePiel = tipoDePiel;
	}

	public List<Ingrediente> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}
	
	
    
    
}
