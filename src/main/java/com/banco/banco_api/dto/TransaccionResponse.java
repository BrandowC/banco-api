package com.banco.banco_api.dto;

import com.banco.banco_api.entity.TipoMovimiento;
import com.banco.banco_api.entity.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransaccionResponse(
        Long id,
        TipoTransaccion tipoTransaccion,
        TipoMovimiento tipoMovimiento,
        BigDecimal monto,
        BigDecimal saldoResultante,
        String referencia,
        LocalDateTime fecha,
        Long cuentaId,
        String numeroCuenta
) {}
