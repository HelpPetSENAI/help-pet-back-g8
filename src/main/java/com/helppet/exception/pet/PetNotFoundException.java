package com.helppet.exception.pet;

/**
 * Exceção lançada quando um pet não é encontrado no sistema.
 * <p>
 * Esta exceção de runtime é disparada quando uma operação tenta acessar
 * um pet que não existe no banco de dados, tipicamente durante operações
 * de consulta, atualização ou exclusão.
 * </p>
 *
 * @author Help Pet Team
 * @version 1.0
 * @since 2026-03-03
 */
public class PetNotFoundException extends RuntimeException {
    
    /**
     * Construtor que cria uma exceção com mensagem personalizada.
     * <p>
     * Gera uma mensagem informando que o pet com o ID especificado não foi encontrado.
     * </p>
     *
     * @param id identificador do pet que não foi encontrado
     */
    public PetNotFoundException(Long id) {
        super("Pet não foi encontrado para o id: " + id);
    }
        /**
     * Construtor que recebe uma mensagem personalizada.
     */
    public PetNotFoundException(String message) {
        super(message);
    }
}
