package com.banco.banco_api.dto;

import com.banco.banco_api.entity.TipoIdentificacion;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClienteRequest(

        @NotNull(message = "El tipo de identificación es obligatorio")
        TipoIdentificacion tipoIdentificacion,

        @NotBlank(message = "El número de identificación es obligatorio")
        @Size(max = 20)
        String numeroIdentificacion,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(min = 2, max = 100, message = "Los nombres deben tener mínimo 2 caracteres")
        String nombres,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 100, message = "El apellido debe tener mínimo 2 caracteres")
        String apellido,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String correoElectronico,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe estar en el pasado")
        LocalDate fechaNacimiento
) {}