package com.helppet.dto.response;

import com.helppet.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe DTO (Data Transfer Object) para respostas relacionadas a Usuários.
 * <p>
 * Esta classe encapsula os dados retornados nas respostas HTTP quando
 * informações de usuários são solicitadas. Define quais dados do usuário
 * serão expostos ao cliente da API, excluindo informações sensíveis.
 * </p>
 *
 * @author Help Pet Team
 * @version 1.0
 * @since 2026-03-03
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    /**
     * Nome do usuário.
     * Informação pública exposta nas respostas da API.
     */
    private String name;
    
    /**
     * E-mail do usuário.
     * Informação exposta nas respostas da API para identificação.
     */
    private String email;

    /**
     * Construtor que cria um UserResponse a partir de uma entidade User.
     * <p>
     * Converte a entidade do banco de dados em um objeto de resposta,
     * expondo apenas nome e e-mail, omitindo dados sensíveis como senha e CPF.
     * </p>
     *
     * @param user entidade User contendo os dados a serem convertidos
     */
    public UserResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
