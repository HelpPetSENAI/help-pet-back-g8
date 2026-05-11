package com.helppet.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Wrapper padrão para TODAS as respostas da API.
 * 
 * Garante formato consistente em sucesso, erro e validação.
 * 
 * Exemplo de resposta de sucesso:
 * {
 *   "success": true,
 *   "message": "Pet criado com sucesso",
 *   "data": { "id": 1, "name": "Bilu", ... },
 *   "correlationId": "550e8400-e29b-41d4-a716-446655440000"
 * }
 * 
 * Exemplo de resposta de erro:
 * {
 *   "success": false,
 *   "message": "Pet não encontrado",
 *   "data": null,
 *   "correlationId": "550e8400-e29b-41d4-a716-446655440000"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String correlationId;

    /**
     * Construtor simplificado (sem correlationId).
     * Usado na maioria dos controllers.
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Factory method para sucesso.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Factory method para erro.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * Factory method para erro com correlationId.
     */
    public static <T> ApiResponse<T> error(String message, String correlationId) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null);
        response.setCorrelationId(correlationId);
        return response;
    }
}
