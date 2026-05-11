package com.helppet.exception;

/**
 * Exceção lançada quando um usuário tenta acessar um pet que não pertence a ele.
 * 
 * Mapeada para: 403 FORBIDDEN no GlobalExceptionHandler
 */
public class UnauthorizedPetAccessException extends RuntimeException {
    
    public UnauthorizedPetAccessException(String message) {
        super(message);
    }

    public UnauthorizedPetAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedPetAccessException(Long petId, Long userId) {
        super("Usuário " + userId + " não tem permissão para acessar o pet " + petId);
    }
}
