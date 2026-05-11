package com.helppet.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Classe responsável por representar erros relacionados a operações com usuários.
 * <p>
 * Esta classe é utilizada para encapsular informações de erro que ocorrem durante
 * o processamento de requisições relacionadas a usuários, fornecendo detalhes como
 * mensagem de erro, código de status HTTP e timestamp.
 * </p>
 *
 * @author Help Pet Team
 * @version 1.0
 * @since 2026-03-03
 */
@AllArgsConstructor
@Getter
@Setter
public class UserError {

    /**
     * Mensagem descritiva do erro ocorrido.
     * <p>
     * Contém uma descrição clara e concisa do problema que ocorreu durante
     * a operação relacionada ao usuário.
     * </p>
     */
    private String message;

    /**
     * Código de status HTTP associado ao erro.
     * <p>
     * Representa o código de status HTTP correspondente ao tipo de erro,
     * como 404 para recurso não encontrado, 400 para requisição inválida, etc.
     * </p>
     */
    private int status;

    /**
     * Data e hora em que o erro ocorreu.
     * <p>
     * Registra o momento exato em que o erro foi gerado, permitindo
     * rastreabilidade e auditoria das operações.
     * </p>
     */
    private LocalDate timestamp;
}
