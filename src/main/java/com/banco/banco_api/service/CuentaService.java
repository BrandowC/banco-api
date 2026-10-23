package com.banco.banco_api.service;

import com.banco.banco_api.dto.CuentaRequest;
import com.banco.banco_api.dto.CuentaResponse;
import com.banco.banco_api.entity.*;
import com.banco.banco_api.exception.RecursoNoEncontradoException;
import com.banco.banco_api.exception.ReglaNegocioException;
import com.banco.banco_api.repository.ClienteRepository;
import com.banco.banco_api.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private static final int LONGITUD_NUMERO = 10;

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public CuentaResponse crear(CuentaRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con id " + request.clienteId()));

        BigDecimal saldoInicial = request.saldoInicial() == null ? BigDecimal.ZERO : request.saldoInicial();

        Cuenta cuenta = Cuenta.builder()
                .tipoCuenta(request.tipoCuenta())
                .numeroCuenta(generarNumero(request.tipoCuenta()))
                .estado(EstadoCuenta.ACTIVA)
                .saldo(saldoInicial)
                .saldoDisponible(saldoInicial)
                .exentaGmf(request.exentaGmf())
                .cliente(cliente)
                .build();

        return aRespuesta(cuentaRepository.save(cuenta));
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listar() {
        return cuentaRepository.findAll().stream().map(this::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listarPorCliente(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream().map(this::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public CuentaResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public CuentaResponse cambiarEstado(Long id, EstadoCuenta nuevoEstado) {
        Cuenta cuenta = buscar(id);

        if (cuenta.getEstado() == EstadoCuenta.CANCELADA) {
            throw new ReglaNegocioException("Una cuenta cancelada no puede cambiar de estado");
        }
        if (nuevoEstado == EstadoCuenta.CANCELADA && cuenta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException("Solo se pueden cancelar cuentas con saldo igual a 0");
        }

        cuenta.setEstado(nuevoEstado);
        return aRespuesta(cuentaRepository.save(cuenta));
    }

    private Cuenta buscar(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada con id " + id));
    }

    private String generarNumero(TipoCuenta tipo) {
        int digitosAleatorios = LONGITUD_NUMERO - tipo.getPrefijo().length();
        String numero;
        do {
            StringBuilder sb = new StringBuilder(tipo.getPrefijo());
            for (int i = 0; i < digitosAleatorios; i++) {
                sb.append(random.nextInt(10));
            }
            numero = sb.toString();
        } while (cuentaRepository.existsByNumeroCuenta(numero));
        return numero;
    }

    private CuentaResponse aRespuesta(Cuenta c) {
        return new CuentaResponse(c.getId(), c.getTipoCuenta(), c.getNumeroCuenta(), c.getEstado(),
                c.getSaldo(), c.getSaldoDisponible(), c.isExentaGmf(), c.getFechaCreacion(),
                c.getFechaModificacion(), c.getCliente().getId());
    }
}