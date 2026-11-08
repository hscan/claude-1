package com.example.demo.controller;

import java.net.URI;

import com.example.demo.model.dto.CentroDistribuicaoPatchRequest;
import com.example.demo.model.dto.CentroDistribuicaoRequest;
import com.example.demo.model.dto.CentroDistribuicaoResponse;
import com.example.demo.service.CentroDistribuicaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cd")
public class CentroDistribuicaoController {

    private final CentroDistribuicaoService service;

    public CentroDistribuicaoController(CentroDistribuicaoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public CentroDistribuicaoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public CentroDistribuicaoResponse atualizar(@PathVariable Long id,
            @Valid @RequestBody CentroDistribuicaoRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}")
    public CentroDistribuicaoResponse atualizarParcialmente(@PathVariable Long id,
            @Valid @RequestBody CentroDistribuicaoPatchRequest request) {
        return service.atualizarParcialmente(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CentroDistribuicaoResponse> criar(@Valid @RequestBody CentroDistribuicaoRequest request) {
        CentroDistribuicaoResponse response = service.criar(request);
        return ResponseEntity.created(URI.create("/cd/" + response.id())).body(response);
    }
}
