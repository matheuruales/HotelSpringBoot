package com.hotel.Hotel.controller;

import com.hotel.Hotel.dto.request.CrearHabitacionRequest;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.service.HabitacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    @PostMapping
    public ResponseEntity<HabitacionResponse> crearHabitacion(
            @Valid @RequestBody CrearHabitacionRequest request,
            UriComponentsBuilder uriBuilder) {
        HabitacionResponse creada = habitacionService.crear(request);
        URI ruta = uriBuilder.path("/api/habitaciones/{id}")
                .buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(ruta).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<HabitacionResponse>> listarHabitaciones() {
        return ResponseEntity.ok(habitacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(habitacionService.obtenerPorId(id));
    }
}
