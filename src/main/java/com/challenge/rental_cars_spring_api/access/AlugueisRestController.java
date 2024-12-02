package com.challenge.rental_cars_spring_api.access;

import com.challenge.rental_cars_spring_api.core.queries.ListarAlugueisQuery;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarCarrosQueryResultItem;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.core.queries.dtos.UploadResponseDTO;
import com.challenge.rental_cars_spring_api.core.services.AluguelService;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/alugueis")
@RequiredArgsConstructor
public class AlugueisRestController {

    private final AluguelService aluguelService;
    private final AluguelRepository aluguelRepository;
    private final ListarAlugueisQuery listarAlugueisQuery;
    private static final Logger logger = LoggerFactory.getLogger(AlugueisRestController.class);

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/processar-arquivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leitura do arquivo .rtn e carga de dados na tabela ALUGUEL com sucesso.", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ListarCarrosQueryResultItem.class))}),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<UploadResponseDTO> carregarArquivo(@RequestParam("file") MultipartFile file) {

        UploadResponseDTO resultado = aluguelService.processarArquivo(file);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorno da lista dos alugueis encontrados com sucesso.", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ListarAlugueisQueryResultItem.class))}),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})})
    public ResponseEntity<Map<String, Object>> listarAlugueis(@RequestParam(required = false) String date, @RequestParam(required = false) String model) {
        logger.info("Listando todos os alugueis");
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("alugueis", aluguelService.listarAlugueis(date,model));
            response.put("totalNaoPago", aluguelService.calcularTotalNaoPago());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao listar alugueis", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao listar alugueis"));
        }
    }
}
