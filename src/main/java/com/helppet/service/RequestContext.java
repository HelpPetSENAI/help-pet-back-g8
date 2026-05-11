package com.helppet.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Armazena contexto da requisição extraído do API Gateway.
 * 
 * Escopo: REQUEST (um por requisição HTTP)
 * Alimentado por: RequestContextFilter
 * 
 * Disponibiliza:
 * - userId: ID do usuário autenticado (extraído do header X-User-Id)
 * - userEmail: Email do usuário (extraído do header X-User-Email)
 * - correlationId: ID único da requisição (extraído do header X-Request-Id)
 */
@Component
@RequestScope
public class RequestContext {
    
    private Long userId;
    private String userEmail;
    private String correlationId;

    /**
     * @return ID do usuário extraído do header X-User-Id
     * @throws RuntimeException se header não estiver presente
     */
    public Long getUserId() {
        if (userId == null) {
            throw new RuntimeException("X-User-Id header não encontrado na requisição. Verifique se o API Gateway está enviando este header.");
        }
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return Email do usuário extraído do header X-User-Email (opcional)
     */
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * @return ID único da requisição para rastreamento distribuído
     * @throws RuntimeException se header não estiver presente
     */
    public String getCorrelationId() {
        if (correlationId == null) {
            throw new RuntimeException("X-Request-Id header não encontrado na requisição. Verifique se o API Gateway está enviando este header.");
        }
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
