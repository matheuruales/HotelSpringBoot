package com.hotel.Hotel.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.OptBoolean;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo",
        requireTypeIdForSubtypes = OptBoolean.FALSE)
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
