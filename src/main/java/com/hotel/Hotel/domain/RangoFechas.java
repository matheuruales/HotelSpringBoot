package com.hotel.Hotel.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Embeddable
public class RangoFechas {

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    protected RangoFechas() {
        // Requerido por JPA.
    }

    public RangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la de inicio");
        }
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public LocalDateTime fechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime fechaFin() {
        return fechaFin;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public long getDias() {
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof RangoFechas rango)) {
            return false;
        }
        return Objects.equals(fechaInicio, rango.fechaInicio)
                && Objects.equals(fechaFin, rango.fechaFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fechaInicio, fechaFin);
    }
}
