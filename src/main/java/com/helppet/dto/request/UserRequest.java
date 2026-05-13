package com.helppet.dto.request;

import com.helppet.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe DTO (Data Transfer Object) para requisições relacionadas a Usuários.
 * <p>
 * Esta classe encapsula os dados enviados nas requisições HTTP para criar
 * ou atualizar informações de usuários. Contém validações para garantir a
 * integridade e segurança dos dados recebidos.
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
public class UserRequest {

    /**
     * Nome completo do usuário.
     * Campo obrigatório que não pode estar vazio.
     */
    @NotBlank(message = "Este campo não deve estar vazio")
    private String name;
    
    /**
     * Endereço de e-mail do usuário.
     * Campo obrigatório e único no sistema.
     */
    @NotBlank(message = "Este campo não deve estar vazio")
    private String email;
    
    /**
     * CPF (Cadastro de Pessoa Física) do usuário.
     * Campo obrigatório e único no sistema.
     */
    @NotBlank(message = "Este campo não deve estar vazio")
    @jakarta.validation.constraints.Pattern(regexp = "^\\d{11}$", message = "O CPF deve conter exatamente 11 caracteres numéricos")
    private String cpf;
    
    /**
     * Senha do usuário para autenticação.
     * Campo obrigatório com validação de tamanho entre 6 e 18 caracteres.
     */
    @NotBlank(message = "Este campo não deve estar vazio")
    @Size(min = 6, max = 18)
    private String password;

    /**
     * Construtor que cria um UserRequest a partir de uma entidade User.
     * <p>
     * Útil para converter uma entidade do banco de dados em um objeto de requisição.
     * </p>
     *
     * @param user entidade User contendo os dados a serem copiados
     */
    public UserRequest(User user){
        this.name = user.getName();
        this.email = user.getEmail();
        this.cpf = user.getCpf();
        this.password = user.getPassword();
    }
}
