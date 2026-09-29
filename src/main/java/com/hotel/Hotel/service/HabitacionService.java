package com.hotel.Hotel.service;

import com.hotel.Hotel.domain.Habitacion;
import com.hotel.Hotel.dto.request.CrearHabitacionEstandarRequest;
import com.hotel.Hotel.dto.request.CrearHabitacionRequest;
import com.hotel.Hotel.dto.request.CrearSuitePresidencialRequest;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.mapper.HabitacionMapper;
import com.hotel.Hotel.repository.HabitacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final HabitacionMapper habitacionMapper;

    public HabitacionService(HabitacionRepository habitacionRepository,
            HabitacionMapper habitacionMapper) {
        this.habitacionRepository = habitacionRepository;
        this.habitacionMapper = habitacionMapper;
    }

    @Transactional
    public HabitacionResponse crear(CrearHabitacionRequest request) {
        Habitacion habitacion;
        if (request instanceof CrearHabitacionEstandarRequest estandar) {
            habitacion = habitacionMapper.toEntity(estandar);
        } else if (request instanceof CrearSuitePresidencialRequest suite) {
            habitacion = habitacionMapper.toEntity(suite);
        } else {
            throw new IllegalArgumentException("Tipo de habitación no soportado");
        }

        return habitacionMapper.toResponse(habitacionRepository.save(habitacion));
    }

    @Transactional
    public HabitacionResponse crearEstandar(CrearHabitacionEstandarRequest request) {
        return habitacionMapper.toResponse(
                habitacionRepository.save(habitacionMapper.toEntity(request)));
    }

    @Transactional
    public HabitacionResponse crearSuite(CrearSuitePresidencialRequest request) {
        return habitacionMapper.toResponse(
                habitacionRepository.save(habitacionMapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public List<HabitacionResponse> listarTodas() {
        return habitacionMapper.toResponseList(habitacionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public HabitacionResponse obtenerPorId(UUID id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada con ID: " + id));
        return habitacionMapper.toResponse(habitacion);
    }
}
