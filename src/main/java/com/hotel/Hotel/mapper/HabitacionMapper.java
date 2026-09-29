package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Habitacion;
import com.hotel.Hotel.domain.HabitacionEstandar;
import com.hotel.Hotel.domain.SuitePresidencial;
import com.hotel.Hotel.dto.request.CrearHabitacionEstandarRequest;
import com.hotel.Hotel.dto.request.CrearSuitePresidencialRequest;
import com.hotel.Hotel.dto.response.HabitacionEstandarResponse;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.dto.response.SuitePresidencialResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HabitacionMapper {

    default HabitacionResponse toResponse(Habitacion habitacion) {
        if (habitacion == null) {
            return null;
        }
        if (habitacion instanceof HabitacionEstandar estandar) {
            return toEstandarResponse(estandar);
        }
        if (habitacion instanceof SuitePresidencial suite) {
            return toSuiteResponse(suite);
        }
        throw new IllegalArgumentException("Tipo de habitación no soportado: "
                + habitacion.getClass().getName());
    }

    @Mapping(target = "estado", expression = "java(habitacion.getEstado().name())")
    HabitacionEstandarResponse toEstandarResponse(HabitacionEstandar habitacion);

    @Mapping(target = "estado", expression = "java(habitacion.getEstado().name())")
    SuitePresidencialResponse toSuiteResponse(SuitePresidencial habitacion);

    List<HabitacionResponse> toResponseList(List<Habitacion> habitaciones);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    HabitacionEstandar toEntity(CrearHabitacionEstandarRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    SuitePresidencial toEntity(CrearSuitePresidencialRequest request);
}
