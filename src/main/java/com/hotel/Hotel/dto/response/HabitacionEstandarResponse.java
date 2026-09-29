package com.hotel.Hotel.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;

@JsonTypeName("ESTANDAR")
public final class HabitacionEstandarResponse extends HabitacionResponse {

    private final int camasIndividuales;

    public HabitacionEstandarResponse(UUID id, String numero, int capacidadMaxima,
            double precioPorNoche, String estado, int camasIndividuales) {
        super(id, numero, capacidadMaxima, precioPorNoche, estado);
        this.camasIndividuales = camasIndividuales;
    }

    public int getCamasIndividuales() { return camasIndividuales; }
}
