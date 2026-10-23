package com.banco.banco_api.repository;

import com.banco.banco_api.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}