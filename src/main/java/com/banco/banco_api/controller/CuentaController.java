package com.banco.banco_api.controller;

import com.banco.banco_api.dto.CuentaRequest;
import com.banco.banco_api.dto.CuentaResponse;
import com.banco.banco_api.dto.EstadoRequest;
import com.banco.banco_api.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponse crear(@Valid @RequestBody CuentaRequest request) {
        return cuentaService.crear(request);
    }

    @GetMapping
    public List<CuentaResponse> listar(@RequestParam(required = false) Long clienteId) {
        return clienteId == null ? cuentaService.listar() : cuentaService.listarPorCliente(clienteId);
    }

    @GetMapping("/{id}")
    public CuentaResponse obtener(@PathVariable Long id) {
        return cuentaService.obtener(id);
    }

    @PatchMapping("/{id}/estado")
    public CuentaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return cuentaService.cambiarEstado(id, request.estado());
    }
}