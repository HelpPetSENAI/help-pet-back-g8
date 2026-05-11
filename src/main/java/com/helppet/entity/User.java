package com.helppet.entity;

import com.helppet.dto.request.UserRequest;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade JPA que representa um Usuario no sistema.
 *
 * IMPORTANTE: O campo password armazena o hash BCrypt da senha.
 * Nunca armazene ou compare senhas em texto plano.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    /**
     * Hash BCrypt da senha. Nunca exposto nas respostas da API (ver UserResponse).
     */
    @Column(nullable = false)
    private String password;

    /**
     * Cria um User a partir do DTO de requisicao.
     * NOTA: a senha ainda nao esta hasheada aqui; o hash e feito no UserService.
     */
    public User(UserRequest dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.cpf = dto.getCpf();
        this.password = dto.getPassword();
    }
}
