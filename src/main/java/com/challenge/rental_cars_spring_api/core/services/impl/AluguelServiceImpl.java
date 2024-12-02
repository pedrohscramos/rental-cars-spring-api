package com.challenge.rental_cars_spring_api.core.services.impl;

import com.challenge.rental_cars_spring_api.core.queries.dtos.UploadResponseDTO;
import com.challenge.rental_cars_spring_api.core.services.AluguelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.core.domain.Cliente;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AluguelServiceImpl implements AluguelService {

    @Autowired
    private AluguelRepository aluguelRepository;
    @Autowired
    private CarroRepository carroRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    private static final Logger logger = LoggerFactory.getLogger(AluguelServiceImpl.class);

    @Override
    public UploadResponseDTO processarArquivo(MultipartFile file) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {

            logger.info("Processando arquivo: {}", file.getOriginalFilename());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    file.getInputStream(),
                    StandardCharsets.UTF_8)
            );
            String linha;
            UploadResponseDTO resultado = new UploadResponseDTO();
            while ((linha = reader.readLine()) != null) {


                resultado.setTotalRegistros(resultado.getTotalRegistros() + 1);
                resultado.setMensagem("Leitura do arquivo .rtn e carga de dados na tabela ALUGUEL com sucesso.");

                Long carroId = Long.parseLong(linha.substring(0, 2).trim());
                Long clienteId = Long.parseLong(linha.substring(2, 4).trim());
                Date dataAluguel = Date.valueOf(LocalDate.parse(linha.substring(4, 12), formatter));
                Date dataDevolucao = Date.valueOf(LocalDate.parse(linha.substring(12, 20), formatter));

                Optional<Carro> optCarro = carroRepository.findById(carroId);
                Optional<Cliente> optCliente = clienteRepository.findById(clienteId);

                if (optCarro.isEmpty()) {
                    logger.warn("Carro com ID {} não encontrado.", carroId);
                    continue;
                }
                if (optCliente.isEmpty()) {
                    logger.warn("Cliente com ID {} não encontrado.", clienteId);
                    continue;
                }

                Carro carro = optCarro.get();
                Cliente cliente = optCliente.get();

                long diasAlugados = dataDevolucao.toLocalDate().toEpochDay() - dataAluguel.toLocalDate().toEpochDay();
                BigDecimal valor = carro.getVlrDiaria().multiply(BigDecimal.valueOf(diasAlugados));

                Aluguel aluguel = new Aluguel(null, carro, cliente, dataAluguel, dataDevolucao, valor, false);
                aluguelRepository.save(aluguel);
            }

            return resultado;

        } catch (Exception e) {
            logger.error("Erro ao processar arquivo", e);
            return null;

        }
    }

    public List<ListarAlugueisQueryResultItem> listarAlugueis(String date, String model) {

        return aluguelRepository.findAll().stream()
                .map(ListarAlugueisQueryResultItem::from)
                .collect(Collectors.toList());
    }

    public BigDecimal calcularTotalNaoPago() {
        return aluguelRepository.findAll().stream()
                .filter(aluguel -> !aluguel.isPago())
                .map(Aluguel::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
