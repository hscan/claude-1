package com.example.demo.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CentroDistribuicaoRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Set<ConstraintViolation<CentroDistribuicaoRequest>> validar(String nome, Double lat, Double lon) {
        return validator.validate(new CentroDistribuicaoRequest(nome, lat, lon));
    }

    @ParameterizedTest
    @CsvSource({"0,0", "90,180", "-90,-180", "-23.55,-46.63"})
    void aceitaCoordenadasValidas(double lat, double lon) {
        assertThat(validar("CD", lat, lon)).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"90.0001,0", "-90.0001,0", "0,180.0001", "0,-180.0001"})
    void rejeitaCoordenadasForaDoIntervalo(double lat, double lon) {
        assertThat(validar("CD", lat, lon)).hasSize(1);
    }

    @Test
    void rejeitaNomeInvalidoECoordenadasNulas() {
        assertThat(validar(" ", null, null)).hasSize(3);
        assertThat(validar("x".repeat(151), 0.0, 0.0)).hasSize(1);
    }
}
