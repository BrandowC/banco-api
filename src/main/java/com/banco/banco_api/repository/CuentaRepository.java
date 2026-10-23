package com.banco.banco_api.repository;

import com.banco.banco_api.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    boolean existsByNumeroCuenta(String numeroCuenta);

    boolean existsByClienteId(Long clienteId);

    List<Cuenta> findByClienteId(Long clienteId);
}