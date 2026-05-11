package com.helppet.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/*
 * Qualquer bean que implemente HealthIndicator é detectado automaticamente
 * pelo Spring Boot Actuator e adicionado ao /actuator/health.
 * O nome exibido no JSON será derivado do nome da classe: "database"
 * (remove o sufixo "HealthIndicator" e converte para camelCase).
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    /*
     * DataSource é configurado automaticamente pelo Spring Boot quando
     * o JPA/Hibernate está presente. O próprio HikariCP (pool de conexões
     * padrão) é injetado aqui — não é preciso criar nada.
     */
    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {

            boolean valida = conn.isValid(2); // timeout de 2 segundos

            if (valida) {
                return Health.up()
                        .withDetail("status", "conectado")
                        .withDetail("banco", conn.getMetaData().getDatabaseProductName())
                        .withDetail("url", conn.getMetaData().getURL())
                        .withDetail("versao", conn.getMetaData().getDatabaseProductVersion())
                        .build();
            }

            return Health.down()
                    .withDetail("status", "conexão inválida — ping falhou")
                    .build();

        } catch (Exception e) {
            /*
             * Health.down(e) inclui a exceção no detalhe do JSON,
             * facilitando diagnóstico sem precisar abrir o log.
             */
            return Health.down(e)
                    .withDetail("status", "falha ao conectar ao banco")
                    .build();
        }
    }
}