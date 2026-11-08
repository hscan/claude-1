package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Atualização parcial: campos ausentes (null) permanecem inalterados; os informados seguem as mesmas regras do cadastro. */
public record CentroDistribuicaoPatchRequest(
        @Pattern(regexp = ".*\\S.*", message = "O nome não pode ser vazio")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @DecimalMin(value = "-90.0", message = "A latitude deve estar entre -90 e 90")
        @DecimalMax(value = "90.0", message = "A latitude deve estar entre -90 e 90")
        Double latitude,

        @DecimalMin(value = "-180.0", message = "A longitude deve estar entre -180 e 180")
        @DecimalMax(value = "180.0", message = "A longitude deve estar entre -180 e 180")
        Double longitude) {

    @JsonIgnore
    @AssertTrue(message = "Informe ao menos um campo: nome, latitude ou longitude")
    public boolean isAlgumCampoInformado() {
        return nome != null || latitude != null || longitude != null;
    }
}
