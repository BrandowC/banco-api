package com.banco.banco_api.controller;

import com.banco.banco_api.dto.OperacionRequest;
import com.banco.banco_api.dto.TransaccionResponse;
import com.banco.banco_api.dto.TransferenciaRequest;
import com.banco.banco_api.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    @PostMapping("/consignaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public TransaccionResponse consignar(@Valid @RequestBody OperacionRequest request) {
        return transaccionService.consignar(request);
    }

    @PostMapping("/retiros")
    @ResponseStatus(HttpStatus.CREATED)
    public TransaccionResponse retirar(@Valid @RequestBody OperacionRequest request) {
        return transaccionService.retirar(request);
    }

    @PostMapping("/transferencias")
    @ResponseStatus(HttpStatus.CREATED)
    public List<TransaccionResponse> transferir(@Valid @RequestBody TransferenciaRequest request) {
        return transaccionService.transferir(request);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<TransaccionResponse> estadoDeCuenta(@PathVariable Long cuentaId) {
        return transaccionService.estadoDeCuenta(cuentaId);
    }
}