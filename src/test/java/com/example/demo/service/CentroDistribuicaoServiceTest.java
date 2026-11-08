package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.demo.exception.CentroDistribuicaoNaoEncontradoException;
import com.example.demo.model.CentroDistribuicao;
import com.example.demo.model.dto.CentroDistribuicaoPatchRequest;
import com.example.demo.model.dto.CentroDistribuicaoRequest;
import com.example.demo.model.dto.CentroDistribuicaoResponse;
import com.example.demo.repository.CentroDistribuicaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CentroDistribuicaoServiceTest {

    @Mock
    CentroDistribuicaoRepository repository;

    @InjectMocks
    CentroDistribuicaoService service;

    private static CentroDistribuicao entidade(Long id, String nome, double lat, double lon) {
        CentroDistribuicao cd = new CentroDistribuicao();
        cd.setId(id);
        cd.setNome(nome);
        cd.setLatitude(lat);
        cd.setLongitude(lon);
        return cd;
    }

    @Test
    void criaSalvandoNomeSemEspacosEDevolvendoResponse() {
        when(repository.save(any(CentroDistribuicao.class))).thenAnswer(inv -> {
            CentroDistribuicao cd = inv.getArgument(0);
            cd.setId(7L);
            return cd;
        });

        CentroDistribuicaoResponse response =
                service.criar(new CentroDistribuicaoRequest("  CD Recife  ", -8.05, -34.9));

        ArgumentCaptor<CentroDistribuicao> captor = ArgumentCaptor.forClass(CentroDistribuicao.class);
        Mockito.verify(repository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("CD Recife");
        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.nome()).isEqualTo("CD Recife");
        assertThat(response.latitude()).isEqualTo(-8.05);
        assertThat(response.longitude()).isEqualTo(-34.9);
    }

    @Test
    void atualizaCamposDoCentroExistente() {
        CentroDistribuicao existente = entidade(1L, "CD Antigo", 0.0, 0.0);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.saveAndFlush(existente)).thenReturn(existente);

        CentroDistribuicaoResponse response =
                service.atualizar(1L, new CentroDistribuicaoRequest("  CD Novo ", 10.5, -20.5));

        assertThat(existente.getNome()).isEqualTo("CD Novo");
        assertThat(existente.getLatitude()).isEqualTo(10.5);
        assertThat(existente.getLongitude()).isEqualTo(-20.5);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("CD Novo");
    }

    @Test
    void atualizarIdInexistenteLancaExcecaoENaoSalva() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(99L, new CentroDistribuicaoRequest("CD", 1.0, 1.0)))
                .isInstanceOf(CentroDistribuicaoNaoEncontradoException.class);
        Mockito.verify(repository, Mockito.never()).saveAndFlush(any());
    }

    @Test
    void patchAlteraApenasCamposInformados() {
        CentroDistribuicao existente = entidade(1L, "CD Antigo", 1.0, 2.0);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.saveAndFlush(existente)).thenReturn(existente);

        CentroDistribuicaoResponse response =
                service.atualizarParcialmente(1L, new CentroDistribuicaoPatchRequest(null, 9.0, null));

        assertThat(existente.getNome()).isEqualTo("CD Antigo");
        assertThat(existente.getLatitude()).isEqualTo(9.0);
        assertThat(existente.getLongitude()).isEqualTo(2.0);
        assertThat(response.latitude()).isEqualTo(9.0);
    }

    @Test
    void patchAplicaTrimNoNome() {
        CentroDistribuicao existente = entidade(1L, "CD Antigo", 1.0, 2.0);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.saveAndFlush(existente)).thenReturn(existente);

        service.atualizarParcialmente(1L, new CentroDistribuicaoPatchRequest("  CD Novo ", null, null));

        assertThat(existente.getNome()).isEqualTo("CD Novo");
        assertThat(existente.getLatitude()).isEqualTo(1.0);
    }

    @Test
    void patchIdInexistenteLancaExcecaoENaoSalva() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarParcialmente(99L, new CentroDistribuicaoPatchRequest("x", null, null)))
                .isInstanceOf(CentroDistribuicaoNaoEncontradoException.class);
        Mockito.verify(repository, Mockito.never()).saveAndFlush(any());
    }

    @Test
    void excluiCentroExistente() {
        CentroDistribuicao existente = entidade(1L, "CD", 0.0, 0.0);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        service.excluir(1L);

        Mockito.verify(repository).delete(existente);
    }

    @Test
    void excluirIdInexistenteLancaExcecaoENaoRemove() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(CentroDistribuicaoNaoEncontradoException.class);
        Mockito.verify(repository, Mockito.never()).delete(any());
    }

    @Test
    void buscaPorIdExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(entidade(1L, "CD Rio", -22.9, -43.2)));

        CentroDistribuicaoResponse response = service.buscarPorId(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("CD Rio");
    }

    @Test
    void buscaPorIdInexistenteLancaExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(CentroDistribuicaoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }
}
