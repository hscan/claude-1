package com.example.demo.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.CentroDistribuicao;
import org.junit.jupiter.api.Test;

class CentroDistribuicaoResponseTest {

    @Test
    void converteEntidadeEmResponse() {
        CentroDistribuicao cd = new CentroDistribuicao();
        cd.setId(3L);
        cd.setNome("CD Manaus");
        cd.setLatitude(-3.1);
        cd.setLongitude(-60.0);

        CentroDistribuicaoResponse response = CentroDistribuicaoResponse.from(cd);

        assertThat(response.id()).isEqualTo(3L);
        assertThat(response.nome()).isEqualTo("CD Manaus");
        assertThat(response.latitude()).isEqualTo(-3.1);
        assertThat(response.longitude()).isEqualTo(-60.0);
    }
}
