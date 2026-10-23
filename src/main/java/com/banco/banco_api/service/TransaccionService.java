package com.banco.banco_api.service;

import com.banco.banco_api.dto.OperacionRequest;
import com.banco.banco_api.dto.TransaccionResponse;
import com.banco.banco_api.dto.TransferenciaRequest;
import com.banco.banco_api.entity.*;
import com.banco.banco_api.exception.RecursoNoEncontradoException;
import com.banco.banco_api.exception.ReglaNegocioException;
import com.banco.banco_api.repository.CuentaRepository;
import com.banco.banco_api.repository.TransaccionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;
    private final EntityManager entityManager;

    @Transactional
    public TransaccionResponse consignar(OperacionRequest request) {
        Cuenta cuenta = bloquear(request.cuentaId());
        Transaccion t = aplicar(cuenta, TipoTransaccion.CONSIGNACION, TipoMovimiento.CREDITO,
                request.monto(), UUID.randomUUID().toString());
        return aRespuesta(t);
    }

    @Transactional
    public TransaccionResponse retirar(OperacionRequest request) {
        Cuenta cuenta = bloquear(request.cuentaId());
        Transaccion t = aplicar(cuenta, TipoTransaccion.RETIRO, TipoMovimiento.DEBITO,
                request.monto(), UUID.randomUUID().toString());
        return aRespuesta(t);
    }

    @Transactional
    public List<TransaccionResponse> transferir(TransferenciaRequest request) {
        Long origenId = request.cuentaOrigenId();
        Long destinoId = request.cuentaDestinoId();

        if (origenId.equals(destinoId)) {
            throw new ReglaNegocioException("La cuenta de origen y destino deben ser distintas");
        }

        // Se bloquea siempre en el mismo orden (por id) para evitar deadlocks
        Long menor = Math.min(origenId, destinoId);
        Long mayor = Math.max(origenId, destinoId);
        Cuenta primera = bloquear(menor);
        Cuenta segunda = bloquear(mayor);
        Cuenta origen = origenId.equals(menor) ? primera : segunda;
        Cuenta destino = origenId.equals(menor) ? segunda : primera;

        String referencia = UUID.randomUUID().toString();
        Transaccion debito = aplicar(origen, TipoTransaccion.TRANSFERENCIA, TipoMovimiento.DEBITO,
                request.monto(), referencia);
        Transaccion credito = aplicar(destino, TipoTransaccion.TRANSFERENCIA, TipoMovimiento.CREDITO,
                request.monto(), referencia);

        return List.of(aRespuesta(debito), aRespuesta(credito));
    }

    @Transactional(readOnly = true)
    public List<TransaccionResponse> estadoDeCuenta(Long cuentaId) {
        if (!cuentaRepository.existsById(cuentaId)) {
            throw new RecursoNoEncontradoException("Cuenta no encontrada con id " + cuentaId);
        }
        return transaccionRepository.findByCuentaIdOrderByFechaDesc(cuentaId)
                .stream().map(this::aRespuesta).toList();
    }

    private Cuenta bloquear(Long id) {
        Cuenta cuenta = entityManager.find(Cuenta.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (cuenta == null) {
            throw new RecursoNoEncontradoException("Cuenta no encontrada con id " + id);
        }
        return cuenta;
    }

    private Transaccion aplicar(Cuenta cuenta, TipoTransaccion tipo, TipoMovimiento movimiento,
                                BigDecimal monto, String referencia) {
        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            throw new ReglaNegocioException("La cuenta " + cuenta.getNumeroCuenta() + " no está activa");
        }

        BigDecimal nuevoSaldo = movimiento == TipoMovimiento.CREDITO
                ? cuenta.getSaldo().add(monto)
                : cuenta.getSaldo().subtract(monto);

        if (nuevoSaldo.signum() < 0) {
            throw new ReglaNegocioException("Saldo insuficiente en la cuenta " + cuenta.getNumeroCuenta());
        }

        cuenta.setSaldo(nuevoSaldo);
        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        return transaccionRepository.save(Transaccion.builder()
                .tipoTransaccion(tipo)
                .tipoMovimiento(movimiento)
                .monto(monto)
                .saldoResultante(nuevoSaldo)
                .referencia(referencia)
                .cuenta(cuenta)
                .build());
    }

    private TransaccionResponse aRespuesta(Transaccion t) {
        return new TransaccionResponse(t.getId(), t.getTipoTransaccion(), t.getTipoMovimiento(),
                t.getMonto(), t.getSaldoResultante(), t.getReferencia(), t.getFecha(),
                t.getCuenta().getId(), t.getCuenta().getNumeroCuenta());
    }
}