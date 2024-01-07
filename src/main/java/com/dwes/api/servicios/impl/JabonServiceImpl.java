package com.dwes.api.servicios.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dwes.api.entidades.Ingrediente;
import com.dwes.api.entidades.Jabon;
import com.dwes.api.entidades.enumerados.TipoDePiel;
import com.dwes.api.repositorios.JabonRepository;
import com.dwes.api.servicios.JabonService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class JabonServiceImpl implements JabonService {

    @Autowired
    private JabonRepository jabonRepository;

	@Override
	public Page<Jabon> findAll(Pageable pageable) {
		return jabonRepository.findAll(pageable);
	}

	@Override
	public Optional<Jabon> findById(Long id) {
		  return jabonRepository.findById(id);
	}

	@Override
	public Jabon save(Jabon jabon) {
		return jabonRepository.save(jabon);
	}

	@Override
	public void deleteById(Long id) {
	    jabonRepository.deleteById(id);
		
	}

	@Override
	public Page<Jabon> findAllByIngredientesContaining(String ingrediente, Pageable pageable) {
	    return jabonRepository.findAllByIngredientesContaining(ingrediente, pageable);
	}

	@Override
	public Page<Jabon> findByTipoDePiel(TipoDePiel tipoDePiel, Pageable pageable) {
		  return jabonRepository.findByTipoDePiel(tipoDePiel, pageable);
	}
	
	public List<Ingrediente> findIngredientesByJabonId(Long jabonId) {
	    return jabonRepository.findById(jabonId)
	            .map(Jabon::getIngredientes)
	            .orElse(Collections.emptyList()); // O manejar la ausencia de jabón de alguna otra manera
	}

	@Override
	public boolean existsById(Long id) {
		return jabonRepository.existsById(id);
	}

}
