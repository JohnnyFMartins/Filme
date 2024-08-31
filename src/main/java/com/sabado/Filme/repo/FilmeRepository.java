package com.sabado.Filme.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sabado.Filme.model.Filme;

public interface FilmeRepository extends JpaRepository <Filme, Long> {
	

}
