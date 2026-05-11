package com.helppet.config;

import com.helppet.filter.RequestContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de Filters para a aplicação.
 * 
 * Registra o RequestContextFilter para ser executado
 * em TODAS as requisições da API.
 */
@Configuration
public class WebConfig {

    /**
     * Registra o RequestContextFilter com prioridade alta.
     * 
     * Executa primeiro (ordem 1) para garantir que:
     * 1. Headers do Gateway são extraídos
     * 2. RequestContext é populado
     * 3. Controllers recebem contexto disponível
     */
    @Bean
    public FilterRegistrationBean<RequestContextFilter> requestContextFilter(RequestContextFilter filter) {
        FilterRegistrationBean<RequestContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        registration.setName("RequestContextFilter");
        return registration;
    }
}
