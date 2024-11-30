package com.challenge.rental_cars_spring_api.core.services;

import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;

import java.math.BigDecimal;
import java.util.List;

public interface AluguelService {
    void processarArquivo(String filePath);
    List<ListarAlugueisQueryResultItem> listarAlugueis();
    BigDecimal calcularTotalNaoPago();
}
