package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Cliente;
import com.hotel.Hotel.domain.Reserva;
import com.hotel.Hotel.dto.request.ActualizarClienteRequest;
import com.hotel.Hotel.dto.request.CrearClienteRequest;
import com.hotel.Hotel.dto.response.ClienteResponse;
import com.hotel.Hotel.dto.response.ClienteResumenResponse;
import com.hotel.Hotel.dto.response.ReservaItemResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteResponse toResponse(Cliente cliente);

    List<ClienteResponse> toResponseList(List<Cliente> clientes);

    @Mapping(target = "totalReservasRealizadas", source = "reservas", qualifiedByName = "totalReservas")
    @Mapping(target = "montoTotalGastado", source = "reservas", qualifiedByName = "montoTotal")
    @Mapping(target = "reservasRecientes", source = "reservas", qualifiedByName = "mapearReservas")
    ClienteResumenResponse toResumenResponse(Cliente cliente);

    @Mapping(target = "idReserva", source = "id")
    @Mapping(target = "numeroHabitacion", source = "habitacion.numero")
    @Mapping(target = "fechaInicio", source = "periodo.fechaInicio")
    @Mapping(target = "fechaFin", source = "periodo.fechaFin")
    @Mapping(target = "estado", expression = "java(reserva.getEstado().name())")
    ReservaItemResponse toReservaItemResponse(Reserva reserva);

    @Named("totalReservas")
    default int totalReservas(List<Reserva> reservas) {
        return reservas == null ? 0 : reservas.size();
    }

    @Named("montoTotal")
    default double montoTotal(List<Reserva> reservas) {
        return reservas == null
                ? 0.0
                : reservas.stream().mapToDouble(Reserva::getCostoTotal).sum();
    }

    @Named("mapearReservas")
    default List<ReservaItemResponse> mapearReservas(List<Reserva> reservas) {
        return reservas == null
                ? List.of()
                : reservas.stream().map(this::toReservaItemResponse).toList();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "penalizaciones", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    void actualizarCliente(ActualizarClienteRequest request, @MappingTarget Cliente cliente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "penalizaciones", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    Cliente toEntity(CrearClienteRequest request);
}
