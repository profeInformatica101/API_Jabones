package com.dwes.api.repositorios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dwes.api.entidades.Jabon;
import com.dwes.api.entidades.enumerados.TipoDePiel;

public interface JabonRepository  extends JpaRepository<Jabon, Long>{
    Page<Jabon> findAllByIngredientesContaining(String ingrediente, Pageable pageable);
    Page<Jabon> findByTipoDePiel(TipoDePiel tipoDePiel, Pageable pageable);
 }
