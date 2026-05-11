package com.helppet.exception.user;

/**
 * Exceção lançada quando um usuário não é encontrado no sistema.
 * <p>
 * Esta exceção de runtime é disparada quando uma operação tenta acessar
 * um usuário que não existe no banco de dados, tipicamente durante operações
 * de consulta, atualização ou exclusão.
 * </p>
 *
 * @author Help Pet Team
 * @version 1.0
 * @since 2026-03-03
 */
public class UserNotFoundException extends RuntimeException {
    
    /**
     * Construtor que cria uma exceção com mensagem personalizada.
     * <p>
     * Gera uma mensagem informando que o usuário com o ID especificado não foi encontrado.
     * </p>
     *
     * @param id identificador do usuário que não foi encontrado
     */
    public UserNotFoundException(Long id) {
        super("User não foi encontrado para o id: " + id );
    }

    /**
     * Construtor que cria uma exceção com mensagem customizada.
     * <p>
     * Permite criar exceções com mensagens personalizadas, útil para buscas por email
     * ou outros critérios além do ID.
     * </p>
     *
     * @param message mensagem de erro personalizada
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
