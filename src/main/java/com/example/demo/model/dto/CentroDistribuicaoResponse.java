package com.example.demo.model.dto;

import java.time.LocalDateTime;

import com.example.demo.model.CentroDistribuicao;

public record CentroDistribuicaoResponse(
        Long id,
        String nome,
        Double latitude,
        Double longitude,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao) {

    public static CentroDistribuicaoResponse from(CentroDistribuicao cd) {
        return new CentroDistribuicaoResponse(cd.getId(), cd.getNome(), cd.getLatitude(), cd.getLongitude(),
                cd.getDataCriacao(), cd.getDataAtualizacao());
    }
}
