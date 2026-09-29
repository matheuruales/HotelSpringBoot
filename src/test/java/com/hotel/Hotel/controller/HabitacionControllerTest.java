package com.hotel.Hotel.controller;

import com.hotel.Hotel.repository.HabitacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HabitacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @BeforeEach
    void limpiarDatos() {
        habitacionRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/habitaciones/suites crea una suite sin exigir discriminador")
    void creaSuiteDesdeEndpointEspecifico() throws Exception {
        mockMvc.perform(post("/api/habitaciones/suites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "numero": "P05-501",
                                  "precioPorNoche": 350.0,
                                  "capacidadMaxima": 2,
                                  "jacuzziPrivado": true,
                                  "incluyeMayordomo": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/habitaciones/")))
                .andExpect(jsonPath("$.tipo").value("SUITE"))
                .andExpect(jsonPath("$.numero").value("P05-501"))
                .andExpect(jsonPath("$.jacuzziPrivado").value(true))
                .andExpect(jsonPath("$.incluyeMayordomo").value(true));
    }

    @Test
    @DisplayName("GET /api/habitaciones conserva los atributos concretos de ambos subtipos")
    void listaHabitacionesPolimorficas() throws Exception {
        crearEstandar();
        crearSuite();

        mockMvc.perform(get("/api/habitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.tipo == 'ESTANDAR')].camasIndividuales").value(2))
                .andExpect(jsonPath("$[?(@.tipo == 'SUITE')].jacuzziPrivado").value(true))
                .andExpect(jsonPath("$[?(@.tipo == 'SUITE')].incluyeMayordomo").value(true));
    }

    @Test
    @DisplayName("POST de suite valida el contrato de entrada")
    void rechazaSuiteInvalida() throws Exception {
        mockMvc.perform(post("/api/habitaciones/suites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "numero": " ",
                                  "precioPorNoche": -1,
                                  "capacidadMaxima": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    private void crearEstandar() throws Exception {
        mockMvc.perform(post("/api/habitaciones/estandares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "numero": "E02-201",
                                  "precioPorNoche": 120.0,
                                  "capacidadMaxima": 2,
                                  "camasIndividuales": 2
                                }
                                """))
                .andExpect(status().isCreated());
    }

    private void crearSuite() throws Exception {
        mockMvc.perform(post("/api/habitaciones/suites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "numero": "P05-501",
                                  "precioPorNoche": 350.0,
                                  "capacidadMaxima": 2,
                                  "jacuzziPrivado": true,
                                  "incluyeMayordomo": true
                                }
                                """))
                .andExpect(status().isCreated());
    }
}
