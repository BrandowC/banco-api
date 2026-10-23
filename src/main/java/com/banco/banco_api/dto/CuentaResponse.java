package com.banco.banco_api.dto;

import com.banco.banco_api.entity.EstadoCuenta;
import com.banco.banco_api.entity.TipoCuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CuentaResponse(
        Long id,
        TipoCuenta tipoCuenta,
        String numeroCuenta,
        EstadoCuenta estado,
        BigDecimal saldo,
        BigDecimal saldoDisponible,
        boolean exentaGmf,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion,
        Long clienteId
) {}