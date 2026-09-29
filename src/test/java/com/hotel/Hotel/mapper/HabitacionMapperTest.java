package com.hotel.Hotel.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.Hotel.domain.HabitacionEstandar;
import com.hotel.Hotel.domain.SuitePresidencial;
import com.hotel.Hotel.dto.request.CrearHabitacionRequest;
import com.hotel.Hotel.dto.request.CrearSuitePresidencialRequest;
import com.hotel.Hotel.dto.response.HabitacionEstandarResponse;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.dto.response.SuitePresidencialResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class HabitacionMapperTest {

    private final HabitacionMapper mapper = Mappers.getMapper(HabitacionMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mapeaCadaSubtipoSinPerderSusAtributos() {
        HabitacionResponse estandar = mapper.toResponse(
                new HabitacionEstandar("101", 2, 180_000, 2));
        HabitacionResponse suite = mapper.toResponse(
                new SuitePresidencial("901", 4, 900_000, true, true));

        assertThat(estandar).isInstanceOf(HabitacionEstandarResponse.class);
        assertThat(((HabitacionEstandarResponse) estandar).getCamasIndividuales()).isEqualTo(2);
        assertThat(suite).isInstanceOf(SuitePresidencialResponse.class);
        assertThat(((SuitePresidencialResponse) suite).isJacuzziPrivado()).isTrue();
    }

    @Test
    void serializaYDeserializaElDiscriminadorTipo() throws Exception {
        HabitacionResponse response = mapper.toResponse(
                new SuitePresidencial("901", 4, 900_000, true, true));

        String json = objectMapper.writerFor(HabitacionResponse.class).writeValueAsString(response);
        CrearHabitacionRequest request = objectMapper.readValue("""
                {
                  "tipo": "SUITE",
                  "numero": "902",
                  "capacidadMaxima": 4,
                  "precioPorNoche": 950000,
                  "incluyeMayordomo": true,
                  "jacuzziPrivado": true
                }
                """, CrearHabitacionRequest.class);

        assertThat(json).contains("\"tipo\":\"SUITE\"");
        assertThat(request).isInstanceOf(CrearSuitePresidencialRequest.class);
    }
}
