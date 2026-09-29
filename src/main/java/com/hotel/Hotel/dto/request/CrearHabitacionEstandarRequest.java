package com.hotel.Hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CrearHabitacionEstandarRequest(
        @NotBlank String numero,
        @Positive int capacidadMaxima,
        @PositiveOrZero double precioPorNoche,
        @Positive int camasIndividuales) implements CrearHabitacionRequest {
}
