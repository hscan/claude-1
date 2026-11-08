package com.example.demo.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void naoEncontradoViraProblemDetail404() {
        ProblemDetail problem = handler.handleNaoEncontrado(new CentroDistribuicaoNaoEncontradoException(5L));

        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getDetail()).contains("5");
    }

    @Test
    void validacaoViraProblemDetail400ComErrosPorCampo() throws Exception {
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "request");
        binding.addError(new FieldError("request", "latitude", "A latitude deve estar entre -90 e 90"));
        binding.addError(new FieldError("request", "latitude", "segunda mensagem ignorada"));
        MethodParameter parameter = new MethodParameter(
                ApiExceptionHandlerTest.class.getDeclaredMethod("dummy", Object.class), 0);

        ProblemDetail problem = handler.handleValidation(new MethodArgumentNotValidException(parameter, binding));

        assertThat(problem.getStatus()).isEqualTo(400);
        assertThat(problem.getProperties()).containsKey("erros");
        @SuppressWarnings("unchecked")
        Map<String, String> erros = (Map<String, String>) problem.getProperties().get("erros");
        assertThat(erros)
                .containsEntry("latitude", "A latitude deve estar entre -90 e 90")
                .hasSize(1);
    }

    @SuppressWarnings("unused")
    private void dummy(Object o) {
    }
}
