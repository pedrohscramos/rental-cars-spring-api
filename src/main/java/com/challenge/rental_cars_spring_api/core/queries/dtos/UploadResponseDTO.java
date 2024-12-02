package com.challenge.rental_cars_spring_api.core.queries.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadResponseDTO {

    private Integer totalRegistros;
    private String mensagem;

    public UploadResponseDTO() {
        this.totalRegistros = 0;
        this.mensagem = "";
    }
}
