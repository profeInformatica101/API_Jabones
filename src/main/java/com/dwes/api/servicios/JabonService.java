package com.dwes.api.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwes.api.entidades.Ingrediente;
import com.dwes.api.entidades.Jabon;
import com.dwes.api.entidades.enumerados.TipoDePiel;

public interface JabonService {
	Page<Jabon> findAll(Pageable pageable);
    Optional<Jabon> findById(Long jabonId);
    Jabon save(Jabon jabon);
    void deleteById(Long jabonId);
    

    // Métodos para paginación y búsqueda
    List<Ingrediente> findIngredientesByJabonId(Long jabonId);
    Page<Jabon> findAllByIngredientesContaining(String ingrediente, Pageable pageable);
    Page<Jabon> findByTipoDePiel(TipoDePiel tipoDePiel, Pageable pageable);
	boolean existsById(Long id);
  
}
