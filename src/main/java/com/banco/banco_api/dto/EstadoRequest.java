package com.banco.banco_api.dto;

import com.banco.banco_api.entity.EstadoCuenta;
import jakarta.validation.constraints.NotNull;

public record EstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoCuenta estado
) {}