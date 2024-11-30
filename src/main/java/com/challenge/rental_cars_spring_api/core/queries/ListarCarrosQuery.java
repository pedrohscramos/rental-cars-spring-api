package com.challenge.rental_cars_spring_api.core.queries;

import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarCarrosQueryResultItem;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarCarrosQuery {

    private final CarroRepository carroRepository;

    public List<ListarCarrosQueryResultItem> execute() {
        if (carroRepository == null) {
            throw new IllegalStateException("CarroRepository is null");
        }

        try {
            return carroRepository
                    .findAll()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(ListarCarrosQueryResultItem::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute ListarCarrosQuery", e);
        }
    }
}