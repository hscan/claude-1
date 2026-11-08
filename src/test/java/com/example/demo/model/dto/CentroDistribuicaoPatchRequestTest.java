package com.example.demo.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class CentroDistribuicaoPatchRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Set<ConstraintViolation<CentroDistribuicaoPatchRequest>> validar(String nome, Double lat, Double lon) {
        return validator.validate(new CentroDistribuicaoPatchRequest(nome, lat, lon));
    }

    @Test
    void aceitaQualquerCampoIsolado() {
        assertThat(validar("CD", null, null)).isEmpty();
        assertThat(validar(null, -90.0, null)).isEmpty();
        assertThat(validar(null, null, 180.0)).isEmpty();
    }

    @Test
    void rejeitaTodosNulos() {
        assertThat(validar(null, null, null)).hasSize(1);
    }

    @Test
    void rejeitaValoresForaDasRegras() {
        assertThat(validar("   ", null, null)).hasSize(1);
        assertThat(validar("x".repeat(151), null, null)).hasSize(1);
        assertThat(validar(null, 90.1, null)).hasSize(1);
        assertThat(validar(null, null, -180.1)).hasSize(1);
    }
}
