package com.eduardo.studymind.exception;


import com.eduardo.studymind.dto.output.erros.DadosErro;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<DadosErro> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DadosErro(404, ex.getMessage()));
    }

    @ExceptionHandler(RegrasDeNegocioException.class)
    public ResponseEntity<DadosErro> handleRegrasDeNegocio(RegrasDeNegocioException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new DadosErro(422, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DadosErro> handleValidacao(MethodArgumentNotValidException ex) {
        var mensagem = ex.getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DadosErro(400, mensagem));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DadosErro> handleErroGenerico(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DadosErro(500, "Erro interno do servidor"));
    }
}
