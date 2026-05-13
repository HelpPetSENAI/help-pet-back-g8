package com.helppet.filter;

import com.helppet.service.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro que extrai headers do API Gateway e popula RequestContext.
 * 
 * Executado UMA VEZ por requisição (antes de chegar ao controller).
 * 
 * Headers esperados do API Gateway:
 * - X-User-Id: ID do usuário autenticado (OBRIGATÓRIO)
 * - X-User-Email: Email do usuário (OPCIONAL)
 * - X-Request-Id: ID único da requisição para rastreamento (OBRIGATÓRIO)
 * 
 * Se algum header obrigatório estiver faltando, a requisição é rejeitada com erro 401/400.
 */
@Component("customRequestContextFilter")
public class RequestContextFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextFilter.class);
    
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_EMAIL = "X-User-Email";
    private static final String HEADER_CORRELATION_ID = "X-Request-Id";
    private static final String HEADER_INTERNAL_TOKEN = "X-Internal-Token";

    @Value("${internal.service.token:CHANGE_ME_INTERNAL_TOKEN}")
    private String expectedInternalToken;

    private final RequestContext requestContext;

    public RequestContextFilter(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Ignorar rotas publicas
        if (path.equals("/actuator/health") || path.startsWith("/api/health")) {
            return true;
        }
        if (path.equals("/api/v1/users/login") && "POST".equalsIgnoreCase(method)) {
            return true;
        }
        if (path.equals("/api/v1/users") && "POST".equalsIgnoreCase(method)) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // ===== Validar token interno entre servicos =====
            String internalToken = request.getHeader(HEADER_INTERNAL_TOKEN);
            if (internalToken == null || !internalToken.equals(expectedInternalToken)) {
                logger.error("Token interno ausente/invalido para requisicao {}", request.getRequestURI());
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Nao autorizado: requisicao nao confiavel entre servicos"
                );
                return;
            }

            // ===== Extrair X-User-Id (OBRIGATÓRIO) =====
            String userIdHeader = request.getHeader(HEADER_USER_ID);
            if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
                logger.error("Header {} não encontrado na requisição {}",
                    HEADER_USER_ID, request.getRequestURI());
                response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Requisição não autenticada: header " + HEADER_USER_ID + " não encontrado ou inválido"
                );
                return;
            }

            Long userId;
            try {
                userId = Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                logger.error("Header {} não é um número válido: {}", HEADER_USER_ID, userIdHeader);
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Header " + HEADER_USER_ID + " deve ser um número inteiro"
                );
                return;
            }

            requestContext.setUserId(userId);
            logger.debug("X-User-Id extraído: {}", userId);

            // ===== Extrair X-User-Email (OPCIONAL) =====
            String userEmail = request.getHeader(HEADER_USER_EMAIL);
            if (userEmail != null && !userEmail.trim().isEmpty()) {
                requestContext.setUserEmail(userEmail);
                logger.debug("X-User-Email extraído: {}", userEmail);
            }

            // ===== Extrair X-Request-Id (OBRIGATÓRIO) =====
            String correlationId = request.getHeader(HEADER_CORRELATION_ID);
            if (correlationId == null || correlationId.trim().isEmpty()) {
                correlationId = UUID.randomUUID().toString();
                logger.warn("Header {} não encontrado, gerando novo: {}", HEADER_CORRELATION_ID, correlationId);
            }
            
            requestContext.setCorrelationId(correlationId);

            // Adicionar ao response header para que o cliente consiga rastrear
            response.addHeader(HEADER_CORRELATION_ID, correlationId);
            logger.debug("X-Request-Id: {} | X-User-Id: {}", correlationId, userId);

        } catch (Exception e) {
            logger.error("Erro ao processar headers da requisição", e);
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Erro ao processar contexto da requisição"
            );
            return;
        }

        // Continuar com a próxima linha da cadeia de filtros
        filterChain.doFilter(request, response);
    }
}
