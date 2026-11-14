package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.CentroDistribuicao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CentroDistribuicaoRepositoryTest {

    @Autowired
    CentroDistribuicaoRepository repository;

    @Test
    void salvaGerandoIdEDatas() {
        CentroDistribuicao cd = new CentroDistribuicao();
        cd.setNome("CD Curitiba");
        cd.setLatitude(-25.43);
        cd.setLongitude(-49.27);

        CentroDistribuicao salvo = repository.saveAndFlush(cd);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getDataCriacao()).isNotNull();
        assertThat(salvo.getDataAtualizacao()).isNotNull();
        assertThat(repository.findById(salvo.getId())).get()
            .hasFieldOrPropertyWithValue("nome", "CD Curitiba");
    }

    @Test
    void removeCentroSalvo() {
        CentroDistribuicao cd = new CentroDistribuicao();
        cd.setNome("CD Temporario");
        cd.setLatitude(0.0);
        cd.setLongitude(0.0);
        CentroDistribuicao salvo = repository.saveAndFlush(cd);

        repository.delete(salvo);
        repository.flush();

        assertThat(repository.findById(salvo.getId())).isEmpty();
    }

    @Test
    void atualizacaoMantemCriacaoEAvancaAtualizacao() throws InterruptedException {
        CentroDistribuicao cd = new CentroDistribuicao();
        cd.setNome("CD Salvador");
        cd.setLatitude(-12.97);
        cd.setLongitude(-38.5);
        CentroDistribuicao salvo = repository.saveAndFlush(cd);
        var criacao = salvo.getDataCriacao();
        var atualizacaoInicial = salvo.getDataAtualizacao();

        Thread.sleep(5);
        salvo.setNome("CD Salvador Norte");
        repository.saveAndFlush(salvo);

        assertThat(salvo.getDataCriacao()).isEqualTo(criacao);
        assertThat(salvo.getDataAtualizacao()).isAfterOrEqualTo(atualizacaoInicial);
    }
}
