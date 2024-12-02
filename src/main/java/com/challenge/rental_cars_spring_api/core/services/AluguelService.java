package com.challenge.rental_cars_spring_api.core.services;

import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.core.queries.dtos.UploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface AluguelService {
    UploadResponseDTO processarArquivo(MultipartFile file);
    List<ListarAlugueisQueryResultItem> listarAlugueis(String date, String model);
    BigDecimal calcularTotalNaoPago();

}
