package com.banco.banco_api.dto;

import com.banco.banco_api.entity.TipoCuenta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CuentaRequest(

        @NotNull(message = "El tipo de cuenta es obligatorio")
        TipoCuenta tipoCuenta,

        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @PositiveOrZero(message = "El saldo inicial no puede ser negativo")
        BigDecimal saldoInicial,

        boolean exentaGmf
) {}