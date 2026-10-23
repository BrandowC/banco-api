package com.banco.banco_api.repository;

import com.banco.banco_api.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
}