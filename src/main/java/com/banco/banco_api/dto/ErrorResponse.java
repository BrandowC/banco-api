package com.banco.banco_api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime fecha,
        int estado,
        String mensaje,
        List<String> detalles
) {}