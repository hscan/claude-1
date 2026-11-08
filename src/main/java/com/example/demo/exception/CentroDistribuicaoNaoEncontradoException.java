package com.example.demo.exception;

public class CentroDistribuicaoNaoEncontradoException extends RuntimeException {

    public CentroDistribuicaoNaoEncontradoException(Long id) {
        super("Centro de distribuição não encontrado: " + id);
    }
}
