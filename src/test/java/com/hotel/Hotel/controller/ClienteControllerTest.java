package com.hotel.Hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.Hotel.domain.Cliente;
import com.hotel.Hotel.dto.request.ActualizarClienteRequest;
import com.hotel.Hotel.dto.request.CrearClienteRequest;
import com.hotel.Hotel.repository.ClienteRepository;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        clienteExistente = new Cliente("Juan Pérez", "juan.perez@email.com");
        clienteRepository.save(clienteExistente);
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id} - Debe actualizar solo el nombre sin afectar el email")
    void debeActualizarSoloNombre() throws Exception {
        // Arrange
        ActualizarClienteRequest request = new ActualizarClienteRequest("Carlos Rodríguez", null);
        String emailOriginal = clienteExistente.getEmail();

        // Act & Assert
        mockMvc.perform(patch("/api/clientes/{id}", clienteExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Rodríguez"))
                .andExpect(jsonPath("$.email").value(emailOriginal))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.penalizaciones").value(0));

        // Verificar en base de datos
        Cliente actualizado = clienteRepository.findById(clienteExistente.getId()).orElseThrow();
        assertThat(actualizado.getNombre()).isEqualTo("Carlos Rodríguez");
        assertThat(actualizado.getEmail()).isEqualTo(emailOriginal);
        assertThat(actualizado.isActivo()).isTrue();
        assertThat(actualizado.getPenalizaciones()).isEqualTo(0);
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id} - Debe actualizar solo el email sin afectar el nombre")
    void debeActualizarSoloEmail() throws Exception {
        // Arrange
        ActualizarClienteRequest request = new ActualizarClienteRequest(null, "nuevo.email@ejemplo.com");
        String nombreOriginal = clienteExistente.getNombre();

        // Act & Assert
        mockMvc.perform(patch("/api/clientes/{id}", clienteExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(nombreOriginal))
                .andExpect(jsonPath("$.email").value("nuevo.email@ejemplo.com"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.penalizaciones").value(0));

        // Verificar en base de datos
        Cliente actualizado = clienteRepository.findById(clienteExistente.getId()).orElseThrow();
        assertThat(actualizado.getNombre()).isEqualTo(nombreOriginal);
        assertThat(actualizado.getEmail()).isEqualTo("nuevo.email@ejemplo.com");
        assertThat(actualizado.isActivo()).isTrue();
        assertThat(actualizado.getPenalizaciones()).isEqualTo(0);
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id} - Debe actualizar ambos campos cuando se envían ambos")
    void debeActualizarAmbosAtributos() throws Exception {
        // Arrange
        ActualizarClienteRequest request = new ActualizarClienteRequest(
                "María González",
                "maria.gonzalez@email.com"
        );

        // Act & Assert
        mockMvc.perform(patch("/api/clientes/{id}", clienteExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("María González"))
                .andExpect(jsonPath("$.email").value("maria.gonzalez@email.com"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.penalizaciones").value(0));

        // Verificar en base de datos
        Cliente actualizado = clienteRepository.findById(clienteExistente.getId()).orElseThrow();
        assertThat(actualizado.getNombre()).isEqualTo("María González");
        assertThat(actualizado.getEmail()).isEqualTo("maria.gonzalez@email.com");
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id} - No debe afectar penalizaciones ni estado activo")
    void noDebeAfectarPenalizacionesNiEstadoActivo() throws Exception {
        // Arrange: Cliente con penalizaciones
        clienteExistente.registrarPenalizacion();
        clienteExistente.registrarPenalizacion();
        clienteRepository.save(clienteExistente);

        ActualizarClienteRequest request = new ActualizarClienteRequest("Nuevo Nombre", null);

        // Act & Assert
        mockMvc.perform(patch("/api/clientes/{id}", clienteExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo Nombre"))
                .andExpect(jsonPath("$.penalizaciones").value(2))
                .andExpect(jsonPath("$.activo").value(true));

        // Verificar en base de datos
        Cliente actualizado = clienteRepository.findById(clienteExistente.getId()).orElseThrow();
        assertThat(actualizado.getPenalizaciones()).isEqualTo(2);
        assertThat(actualizado.isActivo()).isTrue();
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id} - Debe retornar 404 si el cliente no existe")
    void debeRetornar404SiClienteNoExiste() throws Exception {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        ActualizarClienteRequest request = new ActualizarClienteRequest("Nombre", "email@test.com");

        // Act & Assert
        mockMvc.perform(patch("/api/clientes/{id}", idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("Cliente no encontrado")));
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id} - Debe rechazar email con formato inválido")
    void debeRechazarEmailInvalido() throws Exception {
        // Arrange
        ActualizarClienteRequest request = new ActualizarClienteRequest(null, "email-invalido");

        // Act & Assert
        mockMvc.perform(patch("/api/clientes/{id}", clienteExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id} - Debe rechazar nombre vacío")
    void debeRechazarNombreVacio() throws Exception {
        // Arrange
        ActualizarClienteRequest request = new ActualizarClienteRequest("   ", null);

        // Act & Assert
        mockMvc.perform(patch("/api/clientes/{id}", clienteExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/clientes - Debe crear un nuevo cliente")
    void debeCrearNuevoCliente() throws Exception {
        // Arrange
        CrearClienteRequest request = new CrearClienteRequest("Pedro López", "pedro.lopez@email.com");

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nombre").value("Pedro López"))
                .andExpect(jsonPath("$.email").value("pedro.lopez@email.com"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.penalizaciones").value(0));
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Debe obtener un cliente por ID")
    void debeObtenerClientePorId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/clientes/{id}", clienteExistente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteExistente.getId().toString()))
                .andExpect(jsonPath("$.nombre").value(clienteExistente.getNombre()))
                .andExpect(jsonPath("$.email").value(clienteExistente.getEmail()))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    @DisplayName("GET /api/clientes - Debe listar todos los clientes")
    void debeListarTodosLosClientes() throws Exception {
        // Arrange
        Cliente cliente2 = new Cliente("Ana Torres", "ana.torres@email.com");
        clienteRepository.save(cliente2);

        // Act & Assert
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").exists())
                .andExpect(jsonPath("$[1].nombre").exists());
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id} - No debe permitir sobre-asignación de campos protegidos")
    void noDebePermitirSobreAsignacion() throws Exception {
        // Este test verifica que aunque se envíen campos adicionales en JSON,
        // solo se actualicen nombre y email
        String jsonConCamposExtras = """
                {
                    "nombre": "Atacante Malicioso",
                    "email": "atacante@hack.com",
                    "activo": false,
                    "penalizaciones": 999,
                    "id": "00000000-0000-0000-0000-000000000000"
                }
                """;

        UUID idOriginal = clienteExistente.getId();

        // Act & Assert
        mockMvc.perform(patch("/api/clientes/{id}", clienteExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConCamposExtras))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Atacante Malicioso"))
                .andExpect(jsonPath("$.email").value("atacante@hack.com"))
                .andExpect(jsonPath("$.activo").value(true))  // NO debe cambiar
                .andExpect(jsonPath("$.penalizaciones").value(0))  // NO debe cambiar
                .andExpect(jsonPath("$.id").value(idOriginal.toString()));  // NO debe cambiar

        // Verificar en base de datos que los campos protegidos no cambiaron
        Cliente actualizado = clienteRepository.findById(clienteExistente.getId()).orElseThrow();
        assertThat(actualizado.getId()).isEqualTo(idOriginal);
        assertThat(actualizado.isActivo()).isTrue();
        assertThat(actualizado.getPenalizaciones()).isEqualTo(0);
    }
}
