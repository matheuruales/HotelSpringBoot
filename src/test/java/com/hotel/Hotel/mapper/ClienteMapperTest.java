package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Cliente;
import com.hotel.Hotel.domain.EstadoReserva;
import com.hotel.Hotel.domain.Habitacion;
import com.hotel.Hotel.domain.RangoFechas;
import com.hotel.Hotel.domain.Reserva;
import com.hotel.Hotel.dto.request.ActualizarClienteRequest;
import com.hotel.Hotel.dto.response.ClienteResumenResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteMapperTest {

    private final ClienteMapper mapper = Mappers.getMapper(ClienteMapper.class);

    @Test
    void calculaTotalesYGeneraUnaProyeccionSinReferenciaAlCliente() {
        Cliente cliente = mock(Cliente.class);
        Reserva primera = reserva(250_000, "101");
        Reserva segunda = reserva(375_000, "202");
        when(cliente.getReservas()).thenReturn(List.of(primera, segunda));
        when(cliente.getId()).thenReturn(UUID.randomUUID());
        when(cliente.getNombre()).thenReturn("Ana");
        when(cliente.getEmail()).thenReturn("ana@hotel.com");
        when(cliente.isActivo()).thenReturn(true);

        ClienteResumenResponse resumen = mapper.toResumenResponse(cliente);

        assertThat(resumen.totalReservasRealizadas()).isEqualTo(2);
        assertThat(resumen.montoTotalGastado()).isEqualTo(625_000);
        assertThat(resumen.reservasRecientes()).hasSize(2);
        assertThat(resumen.reservasRecientes().get(0).numeroHabitacion()).isEqualTo("101");
        assertThat(resumen.reservasRecientes().get(0).getClass().getRecordComponents())
                .extracting(component -> component.getName())
                .doesNotContain("cliente");
    }

    @Test
    void ignoraCamposNulosDuranteLaActualizacionParcial() {
        Cliente cliente = new Cliente("Nombre original", "anterior@hotel.com");

        mapper.actualizarCliente(new ActualizarClienteRequest(null, "nuevo@hotel.com"), cliente);

        assertThat(cliente.getNombre()).isEqualTo("Nombre original");
        assertThat(cliente.getEmail()).isEqualTo("nuevo@hotel.com");
    }

    private Reserva reserva(double costo, String numeroHabitacion) {
        Reserva reserva = mock(Reserva.class);
        Habitacion habitacion = mock(Habitacion.class);
        when(reserva.getId()).thenReturn(UUID.randomUUID());
        when(reserva.getHabitacion()).thenReturn(habitacion);
        when(habitacion.getNumero()).thenReturn(numeroHabitacion);
        when(reserva.getPeriodo()).thenReturn(new RangoFechas(
                LocalDateTime.of(2026, 10, 1, 15, 0),
                LocalDateTime.of(2026, 10, 3, 11, 0)));
        when(reserva.getEstado()).thenReturn(EstadoReserva.CONFIRMADA);
        when(reserva.getCostoTotal()).thenReturn(costo);
        return reserva;
    }
}
