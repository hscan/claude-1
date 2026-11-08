package com.example.demo.service;

import com.example.demo.exception.CentroDistribuicaoNaoEncontradoException;
import com.example.demo.model.CentroDistribuicao;
import com.example.demo.model.dto.CentroDistribuicaoPatchRequest;
import com.example.demo.model.dto.CentroDistribuicaoRequest;
import com.example.demo.model.dto.CentroDistribuicaoResponse;
import com.example.demo.repository.CentroDistribuicaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CentroDistribuicaoService {

    private final CentroDistribuicaoRepository repository;

    public CentroDistribuicaoService(CentroDistribuicaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CentroDistribuicaoResponse criar(CentroDistribuicaoRequest request) {
        CentroDistribuicao cd = new CentroDistribuicao();
        aplicar(cd, request);
        return CentroDistribuicaoResponse.from(repository.save(cd));
    }

    @Transactional
    public CentroDistribuicaoResponse atualizar(Long id, CentroDistribuicaoRequest request) {
        CentroDistribuicao cd = repository.findById(id)
                .orElseThrow(() -> new CentroDistribuicaoNaoEncontradoException(id));
        aplicar(cd, request);
        return CentroDistribuicaoResponse.from(repository.saveAndFlush(cd));
    }

    @Transactional
    public CentroDistribuicaoResponse atualizarParcialmente(Long id, CentroDistribuicaoPatchRequest request) {
        CentroDistribuicao cd = repository.findById(id)
                .orElseThrow(() -> new CentroDistribuicaoNaoEncontradoException(id));
        if (request.nome() != null) {
            cd.setNome(request.nome().trim());
        }
        if (request.latitude() != null) {
            cd.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            cd.setLongitude(request.longitude());
        }
        return CentroDistribuicaoResponse.from(repository.saveAndFlush(cd));
    }

    @Transactional
    public void excluir(Long id) {
        CentroDistribuicao cd = repository.findById(id)
                .orElseThrow(() -> new CentroDistribuicaoNaoEncontradoException(id));
        repository.delete(cd);
    }

    private void aplicar(CentroDistribuicao cd, CentroDistribuicaoRequest request) {
        cd.setNome(request.nome().trim());
        cd.setLatitude(request.latitude());
        cd.setLongitude(request.longitude());
    }

    @Transactional(readOnly = true)
    public CentroDistribuicaoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(CentroDistribuicaoResponse::from)
                .orElseThrow(() -> new CentroDistribuicaoNaoEncontradoException(id));
    }
}
