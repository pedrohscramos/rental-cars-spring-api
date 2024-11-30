package com.challenge.rental_cars_spring_api.core.queries.dtos;

import com.challenge.rental_cars_spring_api.core.domain.Carro;

public record ListarCarrosQueryResultItem(Long id, String modelo) {


    public static ListarCarrosQueryResultItem from(Carro carro) {
        if (carro == null) {
            throw new NullPointerException("Carro não pode ser null");
        }
        Long id = carro.getId();
        String modelo = carro.getModelo();
        if (id == null || modelo == null) {
            throw new IllegalStateException("Id e modelo do carro não pode ser null");
        }
        return new ListarCarrosQueryResultItem(id, modelo);
    }
}