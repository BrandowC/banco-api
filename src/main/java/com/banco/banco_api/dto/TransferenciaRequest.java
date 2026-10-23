package com.banco.banco_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferenciaRequest(

        @NotNull(message = "La cuenta de origen es obligatoria")
        Long cuentaOrigenId,

        @NotNull(message = "La cuenta de destino es obligatoria")
        Long cuentaDestinoId,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        BigDecimal monto
) {}
