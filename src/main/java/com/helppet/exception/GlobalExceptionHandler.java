package com.helppet.exception;

import com.helppet.dto.response.ApiResponse;
import com.helppet.exception.pet.PetNotFoundException;
import com.helppet.exception.user.UserNotFoundException;
import com.helppet.service.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de excecoes.
 * Centraliza o tratamento de todos os erros garantindo formato ApiResponse consistente.
 *
 * PetNotFoundException            -> 404
 * UserNotFoundException           -> 404
 * UnauthorizedPetAccessException  -> 403
 * MethodArgumentNotValidException -> 400
 * Exception (generica)            -> 500
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final RequestContext requestContext;

    public GlobalExceptionHandler(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePetNotFound(PetNotFoundException ex) {
        String cid = safeGetCorrelationId();
        log.warn("[{}] Pet nao encontrado: {}", cid, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), cid));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
        String cid = safeGetCorrelationId();
        log.warn("[{}] Usuario nao encontrado: {}", cid, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), cid));
    }

    @ExceptionHandler(UnauthorizedPetAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedAccess(UnauthorizedPetAccessException ex) {
        String cid = safeGetCorrelationId();
        log.warn("[{}] Acesso nao autorizado: {}", cid, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), cid));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        String cid = safeGetCorrelationId();
        log.warn("[{}] Falha de validacao", cid);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validacao falhou", errors, cid));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        String cid = safeGetCorrelationId();
        log.error("[{}] Erro interno nao esperado", cid, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erro interno do servidor", cid));
    }

    private String safeGetCorrelationId() {
        try {
            return requestContext.getCorrelationId();
        } catch (RuntimeException e) {
            return "no-correlation-id";
        }
    }
}
