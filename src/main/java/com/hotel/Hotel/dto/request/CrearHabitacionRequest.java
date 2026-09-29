package com.hotel.Hotel.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CrearHabitacionEstandarRequest.class, name = "ESTANDAR"),
        @JsonSubTypes.Type(value = CrearSuitePresidencialRequest.class, name = "SUITE")
})
public sealed interface CrearHabitacionRequest
        permits CrearHabitacionEstandarRequest, CrearSuitePresidencialRequest {
    String numero();

    int capacidadMaxima();

    double precioPorNoche();
}
