package com.banco.banco_api.service;

import com.banco.banco_api.dto.ClienteRequest;
import com.banco.banco_api.dto.ClienteResponse;
import com.banco.banco_api.entity.Cliente;
import com.banco.banco_api.exception.RecursoNoEncontradoException;
import com.banco.banco_api.exception.ReglaNegocioException;
import com.banco.banco_api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private static final int EDAD_MINIMA = 18;

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        validarMayorDeEdad(request.fechaNacimiento());
        if (clienteRepository.existsByNumeroIdentificacion(request.numeroIdentificacion())) {
            throw new ReglaNegocioException("Ya existe un cliente con ese número de identificación");
        }
        Cliente cliente = new Cliente();
        copiarDatos(request, cliente);
        return aRespuesta(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(this::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscar(id);
        validarMayorDeEdad(request.fechaNacimiento());
        if (!cliente.getNumeroIdentificacion().equals(request.numeroIdentificacion())
                && clienteRepository.existsByNumeroIdentificacion(request.numeroIdentificacion())) {
            throw new ReglaNegocioException("Ya existe un cliente con ese número de identificación");
        }
        copiarDatos(request, cliente);
        return aRespuesta(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscar(id);
        // TODO (cuando exista Cuenta): si el cliente tiene productos vinculados,
        // lanzar ReglaNegocioException("No se puede eliminar un cliente con productos vinculados")
        clienteRepository.delete(cliente);
    }

    private Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id " + id));
    }

    private void validarMayorDeEdad(LocalDate fechaNacimiento) {
        if (Period.between(fechaNacimiento, LocalDate.now()).getYears() < EDAD_MINIMA) {
            throw new ReglaNegocioException("El cliente debe ser mayor de edad");
        }
    }

    private void copiarDatos(ClienteRequest r, Cliente c) {
        c.setTipoIdentificacion(r.tipoIdentificacion());
        c.setNumeroIdentificacion(r.numeroIdentificacion());
        c.setNombres(r.nombres());
        c.setApellido(r.apellido());
        c.setCorreoElectronico(r.correoElectronico());
        c.setFechaNacimiento(r.fechaNacimiento());
    }

    private ClienteResponse aRespuesta(Cliente c) {
        return new ClienteResponse(c.getId(), c.getTipoIdentificacion(), c.getNumeroIdentificacion(),
                c.getNombres(), c.getApellido(), c.getCorreoElectronico(), c.getFechaNacimiento(),
                c.getFechaCreacion(), c.getFechaModificacion());
    }
}