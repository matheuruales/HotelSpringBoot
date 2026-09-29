package com.hotel.Hotel.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarClienteRequest(
        @Pattern(regexp = ".*\\S.*", message = "El nombre no puede estar vacío")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Email(message = "El email debe tener un formato válido")
        @Size(max = 120, message = "El email no puede superar 120 caracteres")
        String email) {
}
