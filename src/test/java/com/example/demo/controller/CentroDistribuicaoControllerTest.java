package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import com.example.demo.exception.CentroDistribuicaoNaoEncontradoException;
import com.example.demo.model.dto.CentroDistribuicaoResponse;
import com.example.demo.service.CentroDistribuicaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CentroDistribuicaoController.class)
class CentroDistribuicaoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CentroDistribuicaoService service;

    private static CentroDistribuicaoResponse response(Long id, String nome, double lat, double lon) {
        LocalDateTime agora = LocalDateTime.now();
        return new CentroDistribuicaoResponse(id, nome, lat, lon, agora, agora);
    }

    private ResultActions postJson(String body) throws Exception {
        return mockMvc.perform(post("/cd").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void criaCentroDistribuicaoComDadosValidos() throws Exception {
        when(service.criar(any())).thenReturn(response(1L, "CD São Paulo", -23.55, -46.63));

        postJson("""
                {"nome":"CD São Paulo","latitude":-23.55,"longitude":-46.63}
                """)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/cd/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("CD São Paulo"))
                .andExpect(jsonPath("$.dataCriacao").exists());
    }

    @Test
    void buscaCentroDistribuicaoPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(response(1L, "CD São Paulo", -23.55, -46.63));

        mockMvc.perform(get("/cd/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("CD São Paulo"))
                .andExpect(jsonPath("$.latitude").value(-23.55));
    }

    @Test
    void retorna404QuandoNaoEncontrado() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new CentroDistribuicaoNaoEncontradoException(99L));

        mockMvc.perform(get("/cd/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void retorna400QuandoIdNaoNumerico() throws Exception {
        mockMvc.perform(get("/cd/abc"))
                .andExpect(status().isBadRequest());
    }

    private ResultActions putJson(String path, String body) throws Exception {
        return mockMvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void atualizaCentroDistribuicao() throws Exception {
        when(service.atualizar(eq(1L), any())).thenReturn(response(1L, "CD Novo", 10.0, 20.0));

        putJson("/cd/1", """
                {"nome":"CD Novo","latitude":10,"longitude":20}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("CD Novo"))
                .andExpect(jsonPath("$.longitude").value(20.0));
    }

    @Test
    void atualizacaoRetorna404QuandoNaoEncontrado() throws Exception {
        when(service.atualizar(eq(99L), any())).thenThrow(new CentroDistribuicaoNaoEncontradoException(99L));

        putJson("/cd/99", """
                {"nome":"CD","latitude":10,"longitude":20}
                """)
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizacaoRejeitaDadosInvalidos() throws Exception {
        putJson("/cd/1", """
                {"nome":" ","latitude":95,"longitude":-200}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.latitude").exists())
                .andExpect(jsonPath("$.erros.longitude").exists());
        verify(service, never()).atualizar(any(), any());
    }

    @Test
    void atualizacaoRetorna400QuandoIdNaoNumerico() throws Exception {
        putJson("/cd/abc", """
                {"nome":"CD","latitude":10,"longitude":20}
                """)
                .andExpect(status().isBadRequest());
    }

    private ResultActions patchJson(String path, String body) throws Exception {
        return mockMvc.perform(patch(path).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void atualizaParcialmenteApenasUmCampo() throws Exception {
        when(service.atualizarParcialmente(eq(1L), any())).thenReturn(response(1L, "CD", 10.0, 55.5));

        patchJson("/cd/1", """
                {"longitude":55.5}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.longitude").value(55.5));
    }

    @Test
    void patchRetorna404QuandoNaoEncontrado() throws Exception {
        when(service.atualizarParcialmente(eq(99L), any()))
                .thenThrow(new CentroDistribuicaoNaoEncontradoException(99L));

        patchJson("/cd/99", """
                {"nome":"CD"}
                """)
                .andExpect(status().isNotFound());
    }

    @Test
    void patchRejeitaValoresInvalidos() throws Exception {
        patchJson("/cd/1", """
                {"nome":"  ","latitude":95,"longitude":-200}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.latitude").exists())
                .andExpect(jsonPath("$.erros.longitude").exists());
        verify(service, never()).atualizarParcialmente(any(), any());
    }

    @Test
    void patchRejeitaCorpoSemNenhumCampo() throws Exception {
        patchJson("/cd/1", "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.algumCampoInformado").exists());
    }

    @Test
    void patchRetorna400QuandoIdNaoNumerico() throws Exception {
        patchJson("/cd/abc", """
                {"nome":"CD"}
                """)
                .andExpect(status().isBadRequest());
    }

    @Test
    void excluiCentroDistribuicao() throws Exception {
        mockMvc.perform(delete("/cd/1"))
                .andExpect(status().isNoContent());
        verify(service).excluir(1L);
    }

    @Test
    void exclusaoRetorna404QuandoNaoEncontrado() throws Exception {
        doThrow(new CentroDistribuicaoNaoEncontradoException(99L)).when(service).excluir(99L);

        mockMvc.perform(delete("/cd/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void exclusaoRetorna400QuandoIdNaoNumerico() throws Exception {
        mockMvc.perform(delete("/cd/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aceitaLimitesDasCoordenadas() throws Exception {
        when(service.criar(any())).thenReturn(response(2L, "Polo", 90.0, -180.0));

        postJson("""
                {"nome":"Polo","latitude":90,"longitude":-180}
                """)
                .andExpect(status().isCreated());
    }

    @Test
    void rejeitaLatitudeForaDoIntervalo() throws Exception {
        postJson("""
                {"nome":"CD","latitude":95,"longitude":10}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.latitude").exists());
        verify(service, never()).criar(any());
    }

    @Test
    void rejeitaLongitudeForaDoIntervalo() throws Exception {
        postJson("""
                {"nome":"CD","latitude":10,"longitude":-200}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.longitude").exists());
    }

    @Test
    void rejeitaNomeEmBrancoECoordenadasAusentes() throws Exception {
        postJson("""
                {"nome":"  "}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.latitude").exists())
                .andExpect(jsonPath("$.erros.longitude").exists());
    }
}
